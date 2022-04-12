package services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.databind.JsonNode;

import models.Owner;
import models.Project;
import models.Skill;
import org.springframework.util.StringUtils;

/**
 * ApiServiceInterface contains function declaration used for API call
 */
public interface ApiServiceInterface {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Sends an HTTP request to the API to get a list of projects based on keywords
     *
     * @param query    The query to use for the request (keywords)
     * @param limit    The maximum number of projects to return
     * @param isUpdate Cache flag
     * @return A CompletionStage object containing a Project objects list
     * @author Whole group
     */
    CompletionStage<List<Project>> getProjects(String query, int limit, boolean isUpdate);

    /**
     * Sends an HTTP request to the API to get a list of projects based on skills
     *
     * @param query The query to use for the request (a skill name)
     * @return A CompletionStage object containing a Project objects list
     * @author Yan Ren
     */
    CompletionStage<List<Project>> getSkill(String query);

    /**
     * Sends an HTTP request to the API to get a single project based on its ID
     *
     * @param id The ID of the project being fetched
     * @return A CompletionStage object containing a Project object
     * @author Vincent Marechal
     */
    CompletionStage<Project> getSingleProject(long id);

    /**
     * Sends an HTTP request to the API to get an owner model based on its ID
     * the owner model saves personal information and a project list of maximum 10
     * projects
     *
     * @param owner_id The ID of the employer
     * @author Haoyue Zhang
     * @return A CompletionStage object containing Owner object
     */
    CompletionStage<Owner> getUserInfo(String owner_id);

    /**
     * Sends an HTTP request using a given url and returns the json data from the
     * API response
     *
     * @param url The url to use for the request
     * @return The json data from the API response
     * @author Whole group
     */
    CompletableFuture<Object> sendRequest(String url);

    /**
     * Parse a json response from the API into a list of Project objects
     *
     * @param json The API response (json data containing projects data)
     * @return A list of Project objects from the json data
     * @author Whole group
     */
    static CompletionStage<List<Project>> processAPIResponse(CompletableFuture<Object> json) {
        // Make sure that the request was a success before handling it
        return json.thenApply(response -> {
            List<Project> projects = new ArrayList<>();
            String status = ((JsonNode) response).get("status").asText();
            if ("success".equals(status)) {
                ((JsonNode) response).get("result").get("projects")
                        .forEach(item -> projects.add(createProjectFromJsonNode(item)));
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
    static Project createProjectFromJsonNode(JsonNode projectJson) {
        Project p = new Project(projectJson.get("id").asInt(), projectJson.get("owner_id").asText(),
                dateFormat.format(new Date(projectJson.get("submitdate").asLong() * 1000L)),
                StringUtils.capitalize(projectJson.get("title").asText().toLowerCase()),
                projectJson.get("type").asText().toLowerCase(), new ArrayList<>(),
                projectJson.get("preview_description").asText());
        for (JsonNode skill : projectJson.get("jobs")) {
            p.addSkill(new Skill(skill.get("id").asInt(), skill.get("name").asText()));
        }
        return p;
    }
}
