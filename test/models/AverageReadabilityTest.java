package models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AverageReadabilityTest {
    private AverageReadability averageReadability;

    /**
     * Sets up test fixture
     */
    @Before
    public void init(){
        averageReadability = new AverageReadability(
                119.5,
                8.0);
    }
    /**
     * Tests the Parameterized constructor
     */
    @Test
    public void AverageReadabilityConstructorTest(){
        AverageReadability averageReadability = new AverageReadability(
                119.5,
                8.0);
        assertEquals(0,Double.compare(this.averageReadability.getFKGL(),averageReadability.getFKGL()));
        assertEquals(0,Double.compare(this.averageReadability.getFleschIndex(),averageReadability.getFleschIndex()));
    }
    /**
     * Tests the getters
     */
    @Test
    public void getMethodsTest(){
        assertEquals(0,Double.compare(119.5,averageReadability.getFleschIndex()));
        assertEquals(0,Double.compare(8.0,averageReadability.getFKGL()));
    }

    /**
     * Tears down test fixture
     */
    @After
    public void tearDown(){
        averageReadability = null;
    }
}
