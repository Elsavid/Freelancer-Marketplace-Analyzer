package models;

/**
 * Represents a Skill
 *
 * @author Yan Ren
 */
public class Skill {
    private int id;
    private String name;

    /**
     * Default constructor (empty)
     */
    public Skill() {
        super();
    }

    /**
     * Parameterized constructor
     *
     * @param id   The ID to identify this skill (based on the Freelancer API data)
     * @param name The name of this skill
     */
    public Skill(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Getter for the ID of this skill
     *
     * @return The ID of this skill
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the ID of this skill
     *
     * @param id The new ID to use
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the name of this skill
     *
     * @return The name of this skill
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of this skill
     *
     * @param name The new name to use
     */
    public void setName(String name) {
        this.name = name;
    }

}
