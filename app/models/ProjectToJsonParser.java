package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.util.List;

public class ProjectToJsonParser {

    public static ObjectNode convertToJson(List<Project> projectList) {

        ObjectNode response = Json.newObject();
        ObjectNode projects = Json.newObject();
        projectList.stream().forEach(projectObject -> projects.set(String.valueOf(projectObject.getId()), projectToJson(projectObject)));
        response.set("projects", projects);
        return response;
    }

    private static ObjectNode projectToJson(Project projectObject) {

        ObjectNode projectJson = Json.newObject();
        projectJson.put("owner_id", projectObject.getOwnerId());
        projectJson.put("title", projectObject.getTitle());
        projectJson.put("submitdate", projectObject.getSubmitDate());
        projectJson.put("preview_description", projectObject.getPreviewDescription());

        // Skills list
        ArrayNode skillArray = projectJson.putArray("skills");
        projectObject.getSkills().stream().forEach(skillArray::add);

        // Wors statistics
        ArrayNode wordStatsArray = projectJson.putArray("stats");
        projectObject.getWordStats().entrySet().stream().forEach(e -> wordStatsArray.add(e.getKey() + " - " + e.getValue()));

        return projectJson;
    }
}
