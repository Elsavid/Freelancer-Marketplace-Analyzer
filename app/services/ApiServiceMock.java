package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Owner;
import models.Project;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ApiServiceMock implements ApiServiceInterface {

    /**
     * Mock an HTTP request to the API to get a list of projects based on keywords
     *
     * @param query The query to use for the request (keywords)
     * @param limit The maximum number of projects to return
     * @return A CompletionStage object containing a Project objects list
     */
    public CompletionStage<List<Project>> getProjects(String query, int limit) {
        CompletableFuture<List<Project>> result = new CompletableFuture<>();
        new Thread(() -> {
            Path fileName = Paths.get("./test/resources/getProjects.json");
            Charset charset = Charset.forName("ISO-8859-1");
            String jsonString = "";
            try {
                List<String> lines = Files.readAllLines(fileName, charset);
                for (String line : lines) {
                    jsonString += line;
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(jsonString);
                List<Project> projectList = new ArrayList<>();
                for (JsonNode project : node.get("result").get("projects")) {
                    Project p = new ApiService(null).createProjectFromJsonNode(project);
                    projectList.add(p);
                }
                result.complete(projectList);
            } catch (IOException e) {
                System.out.println(e);
            }
        }).start();

        return result;
    }

    /**
     * Mock an HTTP request to the API to get a list of projects based on skills
     * 
     * @author Yan Ren
     * @param query The query to use for the request (a skill name)
     * @return A CompletionStage object containing a Project objects list
     */
    public CompletionStage<List<Project>> getSkill(String query) {
        CompletableFuture<List<Project>> result = new CompletableFuture<>();
        Path fileName = Paths.get("./test/resources/getSkill.json");
        Charset charset = Charset.forName("ISO-8859-1");
        String jsonString = "";
        try {
            List<String> lines = Files.readAllLines(fileName, charset);
            for (String line : lines) {
                jsonString += line;
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonString);
            List<Project> projectList = new ArrayList<>();
            for (JsonNode project : node.get("result").get("projects")) {
                Project p = new ApiService(null).createProjectFromJsonNode(project);
                projectList.add(p);
            }
            result.complete(projectList);
        } catch (IOException e) {
            System.out.println(e);
        }
        return result;
    }

    /**
     * Sends an HTTP request to the API to get a single project based on its ID
     *
     * @param id The ID of the project being fetched
     * @return A CompletionStage object containing a Project object
     */
    public CompletionStage<Project> getSingleProject(long id) {
        return null;
    }

    /**
     * Sends an HTTP request using a given url and returns the json data from the
     * API response
     * 
     * @param url The url to use for the request
     * @return The json data from the API response
     */
    public CompletableFuture<Object> sendRequest(String url) {
        return null;
    }

    /**
     * Parse a json response from the API into a list of Project objects
     * 
     * @param json The API reponse (json data containing projects data)
     * @return A list of Project objects from the json data
     */
    public CompletionStage<List<Project>> processAPIResponse(CompletableFuture<Object> json) {
        return null;
    }

    public Project createProjectFromJsonNode(JsonNode projectJson) {
        return null;
    }

    @Override
    public CompletionStage<Owner> getUserInfo(String owner_id) {
        return null;
    }
}
