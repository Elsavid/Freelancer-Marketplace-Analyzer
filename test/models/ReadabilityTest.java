package models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Readability class
 *
 * @author Wenshu Li
 */
public class ReadabilityTest {
    private String s;
    private Readability readability;

    /**
     * Sets up test fixture
     */
    @Before
    public void init(){
        s = "Hello, how are you? I'm fine, and you? I'm pretty good. Okay, it's nice to talk to you, bye!";
        readability = new Readability(
                117,
                8,
                "Early",
                s);
    }
    /**
     * Tests the Parameterized constructor
     */
    @Test
    public void readabilityConstructorTest(){
        Readability readability = new Readability(117,
                8,
                "Early",
                s);
        assertNotNull(readability);
        assertEquals(this.readability.getFKGL(),readability.getFKGL());
        assertEquals(this.readability.getFleschIndex(),readability.getFleschIndex());
        assertEquals(this.readability.getContents(),readability.getContents());
        assertEquals(this.readability.getEducationLevel(),readability.getEducationLevel());
    }
    /**
     * Tests the getters
     */
    @Test
    public void getMethodsTest(){
        assertEquals(117,readability.getFleschIndex());
        assertEquals(8,readability.getFKGL());
        assertEquals(8,readability.getFKGL());
        assertEquals("Early",readability.getEducationLevel());
        assertEquals(s,readability.getContents());
    }

    /**
     * Tears down test fixture
     */
    @After
    public void teardown(){
        s = null;
        readability = null;
    }

}
