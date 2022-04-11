package services;

import models.Project;
import models.Readability;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the Readability service
 *
 * @author Wenshu Li
 */
public class ReadabilityServiceTest {
    private String s;
    private String s2;
    private ReadabilityService readabilityService;
    private List<Project> projects;

    /**
     * Sets up the test fixture
     */
    @Before
    public void init() {
        s = "Hello, how are you? I'm fine, and you? I'm pretty good. Okay, it's nice to talk to you, bye!";
        s2 = "What are you doing now?";
        readabilityService = new ReadabilityService();
        projects = Arrays.asList(
                new Project(0, "", null, "", "", new ArrayList<>(),
                        "Hello, how are you? I'm fine, and you? I'm pretty good. Okay, it's nice to talk to you, bye!"),
                new Project(0, "", null, "", "", new ArrayList<>(), "What are you doing now?"));
    }

    /**
     * Tests the average readability of these projects
     */
    @Test
    public void getAvgReadability() {
        assertEquals(0, Double.compare(119.5, readabilityService.getAvgReadability(projects).getFleschIndex()));
        assertEquals(0, Double.compare(-2.5, readabilityService.getAvgReadability(projects).getFKGL()));
    }

    /**
     * Tests the single readability of a project
     */
    @Test
    public void getReadabilityTest() {
        // assertEquals(new Readability(),readabilityService.getReadability(s));
        Readability r1 = readabilityService.getReadability("");
        assertEquals(999, r1.getFleschIndex());
        assertEquals(999, r1.getFKGL());
        assertEquals("can't assess", r1.getEducationLevel());
        assertEquals("", r1.getContents());
        Readability r2 = readabilityService.getReadability(s);
        assertEquals(122, r2.getFleschIndex());
        assertEquals(-3, r2.getFKGL());
        assertEquals("Early", r2.getEducationLevel());
        assertEquals(s, r2.getContents());
    }

    /**
     * Tests the total sentences in a context of a project
     */
    @Test
    public void countTotalSentencesTest() {
        assertEquals(4, readabilityService.countTotalSentences(s));
    }

    /**
     * Tests the total words in a context of a project
     */
    @Test
    public void countTotalWordsTest() {
        assertEquals(19, readabilityService.countTotalWords(s));
    }

    /**
     * Tests the total syllables in a context of a project
     */
    @Test
    public void countTotalSyllablesTest() {
        assertEquals(18, readabilityService.countTotalSyllables(s));
    }

    /**
     * Tests the syllables in a single word
     */
    @Test
    public void countSyllableInSingleWordTest() {
        assertEquals(1, readabilityService.countSyllableInSingleWord("bye"));
    }

    /**
     * Tests the assessment method of education level
     */
    @Test
    public void getEducationLevelTest() {
        assertEquals("Early", readabilityService.getEducationLevel(105));
        assertEquals("5th grade", readabilityService.getEducationLevel(92));
        assertEquals("6th grade", readabilityService.getEducationLevel(82));
        assertEquals("7th grade", readabilityService.getEducationLevel(72));
        assertEquals("8th grade", readabilityService.getEducationLevel(62));
        assertEquals("9th grade", readabilityService.getEducationLevel(52));
        assertEquals("High School", readabilityService.getEducationLevel(42));
        assertEquals("Some College", readabilityService.getEducationLevel(32));
        assertEquals("College Graduate", readabilityService.getEducationLevel(30));
        assertEquals("Law School Graduate", readabilityService.getEducationLevel(0));
    }

    /**
     * Tears down the test fixture
     */
    @After
    public void tearDown() {
        s = null;
        s2 = null;
        readabilityService = null;
        projects = null;
    }
}
