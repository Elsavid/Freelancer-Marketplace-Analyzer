package services;

import models.Project;
import models.Readability;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ReadabilityServiceTest {
    private static final String s = "Hello, how are you? I'm fine, and you? I'm pretty good. Okay, it's nice to talk to you, bye!";
    private static final String s2 = "What are you doing now?";
    ReadabilityService readabilityService = new ReadabilityService();
    List<Project> projects = Arrays.asList(
            new Project(0,"",null,"","",new ArrayList<>(),"Hello, how are you? I'm fine, and you? I'm pretty good. Okay, it's nice to talk to you, bye!"),
            new Project(0,"",null,"","",new ArrayList<>(),"What are you doing now?"));

    @Test
    public void getAvgReadability(){
        System.out.println(readabilityService.getAvgReadability(projects).getFleschIndex());
        System.out.println(readabilityService.getAvgReadability(projects).getFKGL());
        assertEquals(0,Double.compare(119.5,readabilityService.getAvgReadability(projects).getFleschIndex()));
        assertEquals(0,Double.compare(-2.5,readabilityService.getAvgReadability(projects).getFKGL()));
//        assertNotNull(readabilityService.getAvgReadability(projects).getFleschIndex());
//        assertNotNull(readabilityService.getAvgReadability(projects).getFKGL());
    }
    @Test
    public void getReadabilityTest(){
        //assertEquals(new Readability(),readabilityService.getReadability(s));
        Readability r1 = readabilityService.getReadability("");
        assertEquals(999,r1.getFleschIndex());
        assertEquals(999,r1.getFKGL());
        assertEquals("can't assess",r1.getEducationLevel());
        assertEquals("",r1.getContents());
        Readability r2 = readabilityService.getReadability(s);
        assertEquals(122,r2.getFleschIndex());
        assertEquals(-3,r2.getFKGL());
        assertEquals("Early",r2.getEducationLevel());
        assertEquals(s,r2.getContents());
    }

    @Test
    public void countTotalSentencesTest(){
        assertEquals(4,readabilityService.countTotalSentences(s));
    }
    @Test
    public void countTotalWordsTest(){
        assertEquals(19,readabilityService.countTotalWords(s));
    }
    @Test
    public void countTotalSyllablesTest(){
        assertEquals(18,readabilityService.countTotalSyllables(s));
    }
    @Test
    public void countSyllableInSingleWordTest(){
        assertEquals(1,readabilityService.countSyllableInSingleWord("bye"));
    }
    @Test
    public void getEducationLevelTest(){
        assertEquals("Early",readabilityService.getEducationLevel(105));
        assertEquals("5th grade",readabilityService.getEducationLevel(92));
        assertEquals("6th grade",readabilityService.getEducationLevel(82));
        assertEquals("7th grade",readabilityService.getEducationLevel(72));
        assertEquals("8th grade",readabilityService.getEducationLevel(62));
        assertEquals("9th grade",readabilityService.getEducationLevel(52));
        assertEquals("High School",readabilityService.getEducationLevel(42));
        assertEquals("Some College",readabilityService.getEducationLevel(32));
        assertEquals("College Graduate",readabilityService.getEducationLevel(30));
        assertEquals("Law School Graduate",readabilityService.getEducationLevel(0));
    }
}
