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
import models.Skill;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

public class ApiService {

    // TODO Rename "listProjects" as "query"
    public static String listProjects = "https://www.freelancer.com/api/projects/0.1/projects/active?limit=";
    public static String getSkill = "https://www.freelancer.com/api/projects/0.1/projects/active?limit=10&job_details=true&jobs[]=";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Inject
    WSClient ws;

    public CompletionStage<List<Project>> getProjects(String query, int limit) {
        CompletableFuture<Object> resp = sendRequest(listProjects + limit + "&job_details=true&query=\"" + query + "\"");
        return processProjectResponse(resp);
    }

    public CompletionStage<List<Project>> getSkill(String query) {
        CompletableFuture<Object> resp = sendRequest(ApiService.getSkill + query);
        return processProjectResponse(resp);
    }

    public CompletableFuture<Object> sendRequest(String url) {
        WSRequest request = ws.url(url);
        CompletionStage<JsonNode> jsonPromise = request.get()
                .thenApply(r -> r.getBody(WSBodyReadables.instance.json()));
        return jsonPromise.toCompletableFuture().thenApply(json -> json);
    }

    public CompletionStage<List<Project>> processProjectResponse(CompletableFuture<Object> json) {
        List<Project> projects = new ArrayList<>();
        return json.thenApply(response -> {
            ((JsonNode) response).get("result").get("projects").forEach(item -> {

                Project p = new Project(item.get("id").asInt(), item.get("owner_id").asText(), dateFormat.format(new Date(item.get("submitdate").asLong() * 1000L)), item.get("title").asText(), "", new ArrayList<>(), item.get("preview_description").asText());

                for (JsonNode skill : item.get("jobs")) {
                    p.addSkill(new Skill(skill.get("id").asInt(), skill.get("name").asText()));
                }

                projects.add(p);
            });
            return projects;
        });
    }
}
