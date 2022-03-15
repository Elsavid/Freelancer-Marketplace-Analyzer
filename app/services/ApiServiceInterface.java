package services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.databind.JsonNode;

import models.Project;

/**
 * ApiServiceInterface contains function declaration used for API call
 */
public interface ApiServiceInterface {

    /**
     * Sends an HTTP request to the API to get a list of projects based on keywords
     *
     * @param query The query to use for the request (including keywords)
     * @param limit The maximum number of projects to return
     * @return A CompletionStage object containing a Project objects list
     */
    CompletionStage<List<Project>> getProjects(String query, int limit);

    /**
     * Sends an HTTP request to the API to get a list of projects based on skills
     * 
     * @author Yan Ren
     * @param query The query to use for the request (a skill name)
     * @return A CompletionStage object containing a Project objects list
     */
    CompletionStage<List<Project>> getSkill(String query);

    /**
     * Sends an HTTP request to the API to get a single project based on its ID
     *
     * @param id The ID of the project being fetched
     * @return A CompletionStage object containing a Project object
     */
    CompletionStage<Project> getSingleProject(long id);

    /**
     * Sends an HTTP request using a given url and returns the json data from the
     * API response
     * 
     * @param url The url to use for the request
     * @return The json data from the API response
     */
    CompletableFuture<Object> sendRequest(String url);

    /**
     * Parse a json response from the API into a list of Project objects
     *
     * @param json The API response (json data containing projects data)
     * @return A list of Project objects from the json data
     */
    CompletionStage<List<Project>> processAPIResponse(CompletableFuture<Object> json);

    Project createProjectFromJsonNode(JsonNode projectJson);
}
