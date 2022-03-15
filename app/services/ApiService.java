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
import org.springframework.util.StringUtils;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

public class ApiService implements ApiServiceInterface {

    public static String projectQuery = "https://www.freelancer.com/api/projects/0.1/projects/";
    public static String skillQuery = "https://www.freelancer.com/api/projects/0.1/projects/active?limit=10&job_details=true&jobs[]=";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Inject
    WSClient ws;

    /**
     * Sends an HTTP request to the API to get a list of projects based on keywords
     *
     * @param query The query to use for the request (including keywords)
     * @param limit The maximum number of projects to return
     * @return A CompletionStage object containing a Project objects list
     *
     * @author Whole group
     */
    public CompletionStage<List<Project>> getProjects(String query, int limit) {
        CompletableFuture<Object> resp = sendRequest(projectQuery + "active?limit=" + limit + "&job_details=true&query=\"" + query + "\"");
        return processProjectResponse(resp);
    }

    /**
     * Sends an HTTP request to the API to get a list of projects based on skills
     * 
     * @author Yan Ren
     * @param query The query to use for the request (a skill name)
     * @return A CompletionStage object containing a Project objects list
     */
    public CompletionStage<List<Project>> getSkill(String query) {
        CompletableFuture<Object> resp = sendRequest(ApiService.skillQuery + query);
        return processProjectResponse(resp);
    }

    /**
     * Sends an HTTP request to the API to get a single project based on its ID
     *
     * @param id The ID of the project being fetched
     * @return A CompletionStage object containing a Project object
     *
     * @author Vincent Marechal
     */
    public CompletionStage<Project> getSingleProject(long id) {
        CompletableFuture<Object> resp = sendRequest(projectQuery + id);
        // Parse to single Project object
        return resp.thenApply(jsonResp -> createProjectFromJsonNode(((JsonNode) jsonResp).get("result")));
    }

    /**
     * Sends an HTTP request using a given url and returns the json data from the API response
     * @param url The url to use for the request
     * @return The json data from the API response
     *
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
     * @param json The API reponse (json data containing projects data)
     * @return A list of Project objects from the json data
     *
     * @author Whole group
     */
    public CompletionStage<List<Project>> processProjectResponse(CompletableFuture<Object> json) {
        List<Project> projects = new ArrayList<>();
        return json.thenApply(response -> {
            ((JsonNode) response).get("result").get("projects").forEach(item -> projects.add(createProjectFromJsonNode(item)));
            return projects;
        });
    }

    /**
     * Parse a json node containing a project data into a Project object
     * 
     * @param projectJson A json node containing the data of a single project
     * @return A Project object
     *
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
