package services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import models.Project;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

public class ApiService {

    @Inject
    WSClient ws;

    public static String listProjects = "https://www.freelancer.com/api/projects/0.1/projects/active?limit=10&job_details=true";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public CompletionStage<List<Project>> getProjects(String query) {
        CompletableFuture<Object> resp = sendRequest(listProjects + "&query=\"" + query + "\"");
        CompletionStage<List<Project>> projectList = processProjectResponse(resp);
        return projectList;
    }

    public CompletableFuture<Object> sendRequest(String url) {
        System.out.println("[debug] sending request: " + url);
        WSRequest request = ws.url(url);

        CompletionStage<JsonNode> jsonPromise = request.get()
                .thenApply(r -> r.getBody(WSBodyReadables.instance.json()));
        // TODO why double return?
        return jsonPromise.toCompletableFuture().thenApply(json -> {
            return json;
        });
    }

    public CompletionStage<List<Project>> processProjectResponse(CompletableFuture<Object> json) {
        List<Project> projects = new ArrayList<>();
        CompletionStage<List<Project>> jsonPromise = json.thenApply(response -> {
            ((JsonNode) response).get("result").get("projects").forEach(item -> {
                Project p = new Project();
                p.setOwnerId(item.get("owner_id").asText());
                p.setTitle(item.get("title").asText());
                p.setSubmitDate(dateFormat.format(new Date(item.get("submitdate").asLong() * 1000L)));
                for (JsonNode skill : item.get("jobs")) {
                    p.addSkill(skill.get("name").asText());
                }
                projects.add(p);
                // TODO Why return jsonPromise? How does it work?
            });
            return projects;
        });
        return jsonPromise;
    }
}
