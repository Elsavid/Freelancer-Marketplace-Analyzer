package models;

import static models.ProjectToJsonParser.*;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tests the ProjectToJsonParser class
 *
 * @author Vincent Marechal
 */
public class ProjectToJsonParserTest {

    private List<Project> projects;
    final private List<Project> emptyList = new ArrayList<>();
    private Project singleProject;

    /**
     * Instantiates tests fixture before each test (each class attributes)
     */
    @Before
    public void initProjects() {
        projects = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String ownerId = "Owner" + i;
            Date randomDate = new Date(Math.abs(System.currentTimeMillis() - RandomUtils.nextLong()));
            String title = "Project number " + i;
            ArrayList<Skill> skills = new ArrayList<>();
            String previewDescription = "This is the preview description of Project number " + i + ". Here is another sentence.";
            Project p = new Project(i, ownerId, randomDate.toString(), title, "", skills, previewDescription);
            projects.add(p);
        }
        singleProject = new Project(
                12345,
                "Owner",
                "TODAY",
                "Project title",
                "",
                new ArrayList<>(),
                "This is a description with several repeated words. \"Words\" is one of those words."
        );
        singleProject.addSkill(new Skill(1, "bigSkill"));
    }

    /**
     * Tests the projectToJson conversion method (applied on a single Project object)
     */
    @Test
    public void projectToJsonTest() {
        ObjectNode projectJson = projectToJson(singleProject);
        assertEquals(5, projectJson.size());
        assertEquals("Owner", projectJson.get("owner_id").asText());
        assertEquals("Project title", projectJson.get("title").asText());
        assertEquals(1, projectJson.get("skills").size());
        assertEquals("bigSkill", projectJson.get("skills").get(0).get("name").asText());
        // Null project case
        assertEquals(0, projectToJson(null).size());
    }

    /**
     * Tests the convertToJson conversion method (applied on a Project objects list)
     */
    @Test
    public void convertToJsonTest() {
        // Empty list
        ObjectNode emptyResponse = convertToJson(emptyList);
        assertEquals(1, emptyResponse.size());
        assertEquals(ObjectNode.class, emptyResponse.get("projects").getClass());
        assertEquals(0, emptyResponse.get("projects").size());

        // Filled list
        ObjectNode response = convertToJson(projects);
        assertEquals(1, response.size());
        assertEquals(ObjectNode.class, response.get("projects").getClass());
        assertEquals(10, response.get("projects").size());

        projects.stream()
                .forEach(p -> {
                    JsonNode projectJson = response.get("projects").get(p.getId()+"");
                    assertEquals(p.getTitle(), projectJson.get("title").asText());
                    assertEquals(p.getSubmitDate(), projectJson.get("submitdate").asText());

                    assertEquals(ArrayNode.class, projectJson.get("skills").getClass());
                    assertEquals(p.getSkills().size(), projectJson.get("skills").size());
                });
    }


    /**
     * Tears down the tests fixture after each test case
     */
    @After
    public void tearDownProjects() {
        projects = null;
        singleProject = null;
    }
}
