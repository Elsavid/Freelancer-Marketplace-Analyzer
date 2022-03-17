package models;

import org.junit.Test;
import static org.junit.Assert.*;

public class AverageReadabilityTest {
    @Test
    public void AverageReadabilityConstructorTest(){
        AverageReadability averageReadability = new AverageReadability(119.5,8.0);
        assertNotNull(averageReadability);
    }

    @Test
    public void getFleschIndexTest(){
        AverageReadability averageReadability = new AverageReadability(119.5,8.0);
        assertEquals(0,Double.compare(119.5,averageReadability.getFleschIndex()));
    }

    @Test
    public void getFKGLTest(){
        AverageReadability averageReadability = new AverageReadability(119.5,8.0);
        assertEquals(0,Double.compare(8.0,averageReadability.getFKGL()));
    }
}
