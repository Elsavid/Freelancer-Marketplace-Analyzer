package models;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

/**
 * Utility methods to convert Project objects to Json data
 *
 * @author Vincent Marechal
 */
public class ProjectToJsonParser {

    /**
     * Converts a list of Project objects to a Json data node
     *
     * @param projectList The list of Project objects to convert
     * @return A Json node containing the Projects data
     */
    public static ObjectNode convertToJson(List<Project> projectList) {

        ObjectNode response = Json.newObject();
        ObjectNode projects = Json.newObject();
        projectList.stream().forEach(
                projectObject -> projects.set(String.valueOf(projectObject.getId()), projectToJson(projectObject)));
        response.set("projects", projects);
        return response;
    }

    /**
     * Converts a Project object to a Json data node
     *
     * @param projectObject The Project object to convert
     * @return A Json node containing the Project data
     */
    public static ObjectNode projectToJson(Project projectObject) {

        ObjectNode projectJson = Json.newObject();
        if (projectObject != null) {
            projectJson.put("owner_id", projectObject.getOwnerId());
            projectJson.put("title", projectObject.getTitle());
            projectJson.put("submitdate", projectObject.getSubmitDate());
            projectJson.put("preview_description", projectObject.getPreviewDescription());

            // Skills list
            ArrayNode skillArray = projectJson.putArray("skills");
            projectObject.getSkills().stream().forEach(skillObject -> {
                ObjectNode skillJson = Json.newObject();
                skillJson.put("id", skillObject.getId());
                skillJson.put("name", skillObject.getName());
                skillArray.add(skillJson);
            });
        }
        return projectJson;
    }
}
