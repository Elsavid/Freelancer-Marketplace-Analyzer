package models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class ProjectTest {

    private Project project;
    private Project defaultProject;

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

    @Test
    public void finalFieldsTest() {

        assertEquals(12345, project.getId());
        assertEquals(0, defaultProject.getId());

        assertEquals("owner", project.getOwnerId());
        assertEquals("", defaultProject.getOwnerId());

        assertEquals("Today", project.getSubmitDate());
        assertEquals(null, defaultProject.getSubmitDate());

        assertEquals("Title", project.getTitle());
        assertEquals("", defaultProject.getTitle());

        assertEquals("type", project.getType());
        assertEquals("", defaultProject.getType());

        assertEquals("description", project.getPreviewDescription());
        assertEquals("", defaultProject.getPreviewDescription());
    }

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



    @After
    public void tearDown() {
        project = null;
    }
}
