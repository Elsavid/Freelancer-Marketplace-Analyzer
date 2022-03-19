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
import java.util.stream.Collectors;

public class ApiServiceMock implements ApiServiceInterface {

    /**
     * Mocks an HTTP request to the API to get a list of projects based on keywords
     *
     * @param query The query to use for the request (not used here)
     * @param limit The maximum number of projects to return (see getProjects.json)
     * @return A CompletionStage object containing a Project objects list
     *
     * @author Whole group
     */
    public CompletionStage<List<Project>> getProjects(String query, int limit) {

        CompletableFuture<List<Project>> result = new CompletableFuture<>();
        try {
            Path fileName = Paths.get("./test/resources/getProjects.json");
            String jsonString = Files.readAllLines(fileName, Charset.forName("ISO-8859-1")).stream().collect(Collectors.joining());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResult = mapper.readTree(jsonString);
            List<Project> projectList = new ArrayList<>();

            jsonResult.get("result")
                    .get("projects")
                    .spliterator()
                    .forEachRemaining(jsonProject -> projectList.add(ApiServiceInterface.createProjectFromJsonNode(jsonProject)));
            result.complete(projectList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Mocks an HTTP request to the API to get a list of projects based on skills
     *
     * @param query The query to use for the request (a skill name)
     * @return A CompletionStage object containing a Project objects list
     * @author Yan Ren
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
                Project p = ApiServiceInterface.createProjectFromJsonNode(project);
                projectList.add(p);
            }
            result.complete(projectList);
        } catch (IOException e) {
            System.out.println(e);
        }
        return result;
    }

    /**
     * Mocks an HTTP request to the API to get a single project based on its ID
     *
     * @param id The ID of the project being fetched (not used here)
     * @return A CompletionStage object containing a Project object (see singleProject.json)
     * @author Vincent Marechal
     */
    public CompletionStage<Project> getSingleProject(long id) {

        CompletableFuture<Project> result = new CompletableFuture<>();
        try {
            Path fileName = Paths.get("./test/resources/singleProject.json");
            String jsonString = Files.readAllLines(fileName, Charset.forName("ISO-8859-1")).stream().collect(Collectors.joining());
            JsonNode jsonResult = new ObjectMapper().readTree(jsonString);
            result.complete(ApiServiceInterface.createProjectFromJsonNode(jsonResult.get("result")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public CompletableFuture<Object> sendRequest(String url) {
        return null;
    }

    @Override
    public CompletionStage<Owner> getUserInfo(String owner_id) {
        return null;
    }
}
