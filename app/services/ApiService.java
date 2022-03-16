package services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import models.Project;
import models.Skill;
import models.Owner;
import org.springframework.util.StringUtils;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

public class ApiService implements ApiServiceInterface {

    public static String projectQuery = "https://www.freelancer.com/api/projects/0.1/projects/";
    public static String skillQuery = "https://www.freelancer.com/api/projects/0.1/projects/active?limit=10&job_details=true&jobs[]=";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Cache<String, CompletionStage<List<Project>>> cache = Caffeine.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Inject
    WSClient ws;

    /**
     * Sends an HTTP request to the API to get a list of projects based on keywords
     *
     * @param query The query to use for the request (including keywords)
     * @param limit The maximum number of projects to return
     * @return A CompletionStage object containing a Project objects list
     * @author Whole group
     */
    public CompletionStage<List<Project>> getProjects(String query, int limit) {
        String finalUrl = projectQuery + "active?limit=" + limit + "&job_details=true&query=\"" + query + "\"";

        // CompletableFuture<Object> resp = sendRequest(finalUrl);
        return cache.get(finalUrl, str -> processAPIResponse(sendRequest(finalUrl)));
    }

    /**
     * Sends an HTTP request to the API to get a list of projects based on skills
     *
     * @param query The query to use for the request (a skill name)
     * @return A CompletionStage object containing a Project objects list
     * @author Yan Ren
     * @author Yan Ren
     */
    public CompletionStage<List<Project>> getSkill(String query) {
        CompletableFuture<Object> resp = sendRequest(ApiService.skillQuery + query);
        return processAPIResponse(resp);
    }

    /**
     * Sends an HTTP request to the API to get a single project based on its ID
     *
     * @param id The ID of the project being fetched
     * @return A CompletionStage object containing a Project object
     * @author Vincent Marechal
     */
    public CompletionStage<Project> getSingleProject(long id) {
        CompletableFuture<Object> resp = sendRequest(projectQuery + id);
        // Parse to single Project object
        return resp.thenApply(jsonResp -> createProjectFromJsonNode(((JsonNode) jsonResp).get("result")));
    }

    /**
     * Sends an HTTP request to the API to get a owner model based on its ID
     * the onwer model saves personal information and a project list of maximum 10 projects
     *
     * @param owner_id The ID of the employer
     * @return A CompletionStage Object containing a Owner object
     *
     * @author Haoyue Zhang
     */
    public CompletionStage<Owner> getUserInfo(String owner_id) {
        return ws.url("https://www.freelancer.com/api/users/0.1/users/" + owner_id).get()
                .thenCombine(ws.url("https://www.freelancer.com/api/projects/0.1/projects/?owners[]=" + owner_id + "&limit=10&job_details=true").get(),
                        (r1, r2) -> {
                            return new Owner(r1.getBody(), r2.getBody());
                        });
    }

    /**
     * Sends an HTTP request using a given url and returns the json data from the API response
     *
     * @param url The url to use for the request
     * @return The json data from the API response
     * @author Whole group
     */
    public CompletableFuture<Object> sendRequest(String url) {
        WSRequest request = ws.url(url);
        CompletionStage<JsonNode> jsonPromise = request.get()
                .thenApply(r -> r.getBody(WSBodyReadables.instance.json()));
        return jsonPromise.toCompletableFuture().thenApply(json -> json);
    }

    /**
     * Parse a json response from the API into a list of Project objects
     *
     * @param json The API reponse (json data containing projects data)
     * @return A list of Project objects from the json data
     * @author Whole group
     */
    public CompletionStage<List<Project>> processAPIResponse(CompletableFuture<Object> json) {
        // Make sure that the request was a success before handling it
        return json.thenApply(response -> {
            List<Project> projects = new ArrayList<>();
            String status = ((JsonNode) response).get("status").asText();
            if ("success".equals(status)) {
                ((JsonNode) response).get("result").get("projects").forEach(item -> projects.add(createProjectFromJsonNode(item)));
            }
            return projects;
        });
    }

    /**
     * Parse a json node containing a project data into a Project object
     *
     * @param projectJson A json node containing the data of a single project
     * @return A Project object
     * @author Whole group
     */
    public Project createProjectFromJsonNode(JsonNode projectJson) {
        Project p = new Project(projectJson.get("id").asInt(), projectJson.get("owner_id").asText(), dateFormat.format(new Date(projectJson.get("submitdate").asLong() * 1000L)), StringUtils.capitalize(projectJson.get("title").asText().toLowerCase()), "", new ArrayList<>(), projectJson.get("preview_description").asText());
        for (JsonNode skill : projectJson.get("jobs")) {
            p.addSkill(new Skill(skill.get("id").asInt(), skill.get("name").asText()));
        }
        return p;
    }
}
