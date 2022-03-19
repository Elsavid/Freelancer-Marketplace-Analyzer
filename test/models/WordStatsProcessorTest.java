package models;

import org.apache.commons.lang3.RandomUtils;
import org.junit.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static models.WordStatsProcessor.*;
import static org.junit.Assert.*;

/**
 * Tests the word stats processing feature
 *
 * @author Vincent Marechal
 */
public class WordStatsProcessorTest {

    private List<Project> projects;
    final private List<Project> emptyList = new ArrayList<>();
    private Project singleProject;

    /**
     * Instantiates tests fixture before each test case (Project object and Project list attributes)
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
    }

    /**
     * Tests the globalWordsStats method for global statistics computing on a Project list
     */
    @Test
    public void getGlobalWordStatsTest() {
        Map<String, Long> globalStats = getGlobalWordStats(projects);
        assertEquals(21, globalStats.entrySet().size());
        globalStats.entrySet().stream().forEach(entry -> {
            /* Two possible cases: either the word is that entry is a number (project ID)
            or it is a word from the sentence "This is the preview description of Project number"
            Because of that, the Entry value is either 1 or 10 */
            long frequency;
            try {
                // If you can parse it to an int, it is a project ID
                Integer.parseInt(entry.getKey());
                frequency = 1;
            } catch (NumberFormatException nfe) {
                if (entry.getKey().equals("is")) {
                    frequency = 20;
                } else {
                    // Else, it is a regular word appearing 10 times
                    frequency = 10;
                }
            }
            assertEquals(frequency, entry.getValue().longValue());
        });
        Map<String, Long> emptyStats = getGlobalWordStats(emptyList);
        assertEquals(0, emptyStats.entrySet().size());
    }

    /**
     * Tests the processProjectWordStats method for single-project statistics computing
     */
    @Test
    public void processProjectWordStatsTest() {
        assertNull(processProjectWordStats(null));
        processProjectWordStats(singleProject);
        assertEquals(11, singleProject.getWordStats().entrySet().size());
        singleProject.getWordStats()
                .entrySet()
                .stream()
                .forEach(entry -> {
                    long frequency;
                    if (entry.getKey().equals("is")) {
                        frequency = 2;
                    } else if (entry.getKey().equals("words")) {
                        frequency = 3;
                    } else {
                        frequency = 1;
                    }
                    assertEquals(frequency, entry.getValue().longValue());
                });
    }

    /**
     * Tests the mapToHtmlTable method for words statistics Map to HTML code conversion
     */
    @Test
    public void mapToHtmlTableTest() {
        assertEquals("", mapToHtmlTable(null));
        processProjectWordStats(singleProject);
        String statsTable = mapToHtmlTable(singleProject.getWordStats());
        assertTrue(statsTable.startsWith("<table class=\"wordStatsTable\"><thead><tr><th>Word</th><th>Number of appearances</th></tr></thead>"));
        assertTrue(statsTable.contains("<tr><td>words</td><td>3</td></tr>"));
        assertTrue(statsTable.contains("<tr><td>is</td><td>2</td></tr>"));
        assertTrue(statsTable.contains("<tr><td>with</td><td>1</td></tr>"));
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
