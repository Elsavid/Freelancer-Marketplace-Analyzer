package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import models.Owner;
import models.Project;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * ApiService contains concrete implementation for ApiServiceInterface
 */
public class ApiService implements ApiServiceInterface {

    public static String projectQuery = "https://www.freelancer.com/api/projects/0.1/projects/";
    public static String skillQuery = "https://www.freelancer.com/api/projects/0.1/projects/active?limit=10&job_details=true&jobs[]=";
    public static String userQuery = "https://www.freelancer.com/api/users/0.1/users/";
    public static String userProjectQuery = "https://www.freelancer.com/api/projects/0.1/projects/?active&owners[]=";
    private final Cache<String, CompletionStage<List<Project>>> cache = Caffeine.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    WSClient ws;

    /**
     * ApiService constructor
     *
     * @param ws The WS client used for this API calls
     * @author Whole group
     */
    @Inject
    public ApiService(WSClient ws) {
        this.ws = ws;
    }

    /**
     * Sends an HTTP request to the API to get a list of projects based on keywords
     *
     * @param query The query to use for the request (keywords)
     * @param limit The maximum number of projects to return
     * @return A CompletionStage object containing a Project objects list
     * @author Whole group
     */
    public CompletionStage<List<Project>> getProjects(String query, int limit, boolean isUpdate) {
        String finalUrl = projectQuery + "active?limit=" + limit + "&job_details=true&query=\"" + query + "\"";
        // First case: we want to fetch new projects (to update the front-page) so we first force update the cache
        if (isUpdate) {
            cache.put(finalUrl, ApiServiceInterface.processAPIResponse(sendRequest(finalUrl)));
        }
        // Second case, we just want to fetch projects already processed before
        return cache.get(finalUrl, str -> ApiServiceInterface.processAPIResponse(sendRequest(str)));
    }

    /**
     * Sends an HTTP request to the API to get a list of projects based on skills
     *
     * @param query The query to use for the request (a skill name)
     * @return A CompletionStage object containing a Project objects list
     * @author Yan Ren
     */
    public CompletionStage<List<Project>> getSkill(String query) {
        CompletableFuture<Object> resp = sendRequest(ApiService.skillQuery + query);
        return ApiServiceInterface.processAPIResponse(resp);
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
        return resp.thenApply(
                jsonResp -> ApiServiceInterface.createProjectFromJsonNode(((JsonNode) jsonResp).get("result")));
    }

    /**
     * Sends an HTTP request to the API to get an owner model based on its ID
     * the owner model saves personal information and a project list of maximum 10
     * projects
     *
     * @param owner_id The ID of the employer
     * @author Haoyue Zhang
     * @return A CompletionStage object containing a Owner objects cantains userinfo and project lists
     */
    public CompletionStage<Owner> getUserInfo(String owner_id) {
        String url1 = userQuery + owner_id;
        return ws.url(url1).get()
                .thenCombine(ws.url(userProjectQuery + owner_id + "&limit=10&job_details=true").get(),
                        (r1, r2) -> new Owner(r1.getBody(), r2.getBody()));
    }

    /**
     * Sends an HTTP request using a given url and returns the json data from the
     * API response
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
}
