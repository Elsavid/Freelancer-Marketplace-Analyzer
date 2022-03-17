package models;

import org.junit.Test;
import static org.junit.Assert.*;

public class ReadabilityTest {
    private static final String s = "Hello, how are you? I'm fine, and you? I'm pretty good. Okay, it's nice to talk to you, bye!";

    @Test
    public void readabilityConstructorTest(){
        Readability readability = new Readability(117,8,"Early",s);
        assertNotNull(readability);
    }

    @Test
    public void getFleschIndexTest(){
        Readability readability = new Readability(117,8,"Early",s);
        assertEquals(117,readability.getFleschIndex());
    }

    @Test
    public void getFKGLTest(){
        Readability readability = new Readability(117,8,"Early",s);
        assertEquals(8,readability.getFKGL());
    }

    @Test
    public void getEducationLevel(){
        Readability readability = new Readability(117,8,"Early",s);
        assertEquals("Early",readability.getEducationLevel());
    }

    @Test
    public void getContentsTest(){
        Readability readability = new Readability(117,8,"Early",s);
        assertEquals(s,readability.getContents());
    }

}
