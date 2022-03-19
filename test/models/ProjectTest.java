package models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Tests for the Project class
 *
 * @author Vincent Marechal
 */
public class ProjectTest {

    private Project project;
    private Project defaultProject;


    /**
     * Instantiates the two Project attributes of this class before each test (test fixture)
     */
    @Before
    public void initProject() {
        project = new Project(
                12345,
                "owner",
                "Today",
                "Title",
                "type",
                new ArrayList<>(),
                "description"
        );
        defaultProject = new Project();
    }

    /**
     * Tests the getters of each final attribute of the Project class
     */
    @Test
    public void finalFieldsTest() {

        assertEquals(12345, project.getId());
        assertEquals(0, defaultProject.getId());

        assertEquals("owner", project.getOwnerId());
        assertEquals("", defaultProject.getOwnerId());

        assertEquals("Today", project.getSubmitDate());
        assertNull(defaultProject.getSubmitDate());

        assertEquals("Title", project.getTitle());
        assertEquals("", defaultProject.getTitle());

        assertEquals("type", project.getType());
        assertEquals("", defaultProject.getType());

        assertEquals("description", project.getPreviewDescription());
        assertEquals("", defaultProject.getPreviewDescription());
    }

    /**
     * Tests the getter and setter of the skills attribute of the Project class
     */
    @Test
    public void skillsTest() {
        assertEquals(0, project.getSkills().size());

        Skill s1 = new Skill(1, "skill 1");
        Skill s2 = new Skill(2, "skill 2");
        Skill s3 = new Skill(3, "skill 3");

        project.addSkill(s1);
        project.addSkill(s2);
        assertEquals(2, project.getSkills().size());
        assertEquals(s1, project.getSkills().get(0));

        ArrayList<Skill> newSkills = new ArrayList<>();
        newSkills.add(s3);
        newSkills.add(s1);
        project.setSkills(newSkills);
        assertEquals(2, project.getSkills().size());
        assertEquals(s3, project.getSkills().get(0));

        assertEquals(0, defaultProject.getSkills().size());
    }

    /**
     * Tests the equals method from the Project class
     */
    @Test
    public void testEquals() {

        Project p1 = new Project(
                12345,
                "owner",
                "Today",
                "Title",
                "type",
                new ArrayList<>(),
                "description"
        );

        Project p2 = new Project(
                6789,
                "owner",
                "Today",
                "New title",
                "type",
                new ArrayList<>(),
                "Another description"
        );

        assertTrue(project.equals(p1));
        assertFalse(project.equals(p2));
        assertFalse(project.equals(defaultProject));
        assertFalse(project.equals(null));
    }

    /**
     * Tears down the tests fixture after each test case
     */
    @After
    public void tearDown() {
        project = null;
    }
}
