package models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SkillTest {
    
    /**
     * Tests for getters and setter of Skill class
     */
    @Test
    public void skillTest() {
        Skill s = new Skill();
        s.setId(1);
        s.setName("test");

        assertEquals(1, s.getId());
        assertEquals("test", s.getName());

        s = new Skill(1, "test");
        assertEquals(1, s.getId());
        assertEquals("test", s.getName());
    }
}
