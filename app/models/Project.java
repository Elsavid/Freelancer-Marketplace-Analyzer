package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Whole group
 */
public class Project {

    final private int id;
    final private String ownerId;
    final private String submitDate;
    private final String title;
    private final String type;
    private ArrayList<Skill> skills;
    private final String previewDescription;
    private Map<String, Long> wordStats;

    /**
     * Default constructor: 0, "" or null values for all attributes, Skills is an empty array
     */
    public Project() {
        this(0, "", null, "", "", new ArrayList<>(), "");
    }

    /**
     * Parameterized constructor
     *
     * @param id                 ID of this project
     * @param ownerId            ID of the owner of this project
     * @param submitDate         Date when this project was submitted to Freelancer
     * @param title              Title of this project
     * @param type               Type of this project
     * @param skills             Skills needed for this project (ArrayList object)
     * @param previewDescription Short description of this project
     */
    public Project(int id, String ownerId, String submitDate, String title, String type, ArrayList<Skill> skills, String previewDescription) {
        this.id = id;
        this.ownerId = ownerId;
        this.submitDate = submitDate;
        this.title = title;
        this.type = type;
        this.skills = skills;
        this.previewDescription = previewDescription;
        this.wordStats = new HashMap<>();
    }

    /**
     * Getter for the ID of this project
     *
     * @return The ID of this project
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the ID of the owner of this project
     *
     * @return The ID of the owner of this project
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Getter for the submission date of this project
     *
     * @return The submission date of this project (a String)
     */
    public String getSubmitDate() {
        return submitDate;
    }

    /**
     * Getter for the title of this project
     *
     * @return The title of this project
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the type of this project
     *
     * @return The type of this project
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for the skills of this project
     *
     * @return The skills needed for this project
     */
    public ArrayList<Skill> getSkills() {
        return skills;
    }

    /**
     * Setter for the skills of this project
     *
     * @param skills The new skills needed for this project (ArrayList)
     */
    public void setSkills(ArrayList<Skill> skills) {
        this.skills = skills;
    }

    /**
     * Add a skill to the list of skills needed for this project
     *
     * @param skill The skill to add to the list of skills needed for this project
     */
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    /**
     * Getter for the preview description of this project
     *
     * @return The preview description of this project
     */
    public String getPreviewDescription() {
        return previewDescription;
    }

    /**
     * Getter for the words statistics of this project
     *
     * @return A Map object containing the words statistics for this project (word, number of appearances)
     */
    public Map<String, Long> getWordStats() {
        return wordStats;
    }

    /**
     * Setter for the words statistics of this project
     *
     * @param wordStats The new Map object containing the words statistics of this project (word, number of appearances)
     */
    public void setWordStats(Map<String, Long> wordStats) {
        this.wordStats = wordStats;
    }

    /**
     * Checks that two Projects are equal
     *
     * @param o An Object to compare this with
     * @return True if equals, false otherwise
     */
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(this.getClass())) {
            return false;
        } else {
            Project p = (Project) o;
            return (p.getId() == this.getId()
                    && p.getOwnerId().equals(this.getOwnerId())
                    && p.getTitle().equals(this.getTitle()))
                    && p.getSubmitDate().equals(this.getSubmitDate())
                    && p.getPreviewDescription().equals(this.getPreviewDescription())
                    && p.getType().equals(this.getType());
        }
    }
}
