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

    public Project() {
        this(0,"",null,"","",new ArrayList<>(),"");
    }

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

    public int getId() { return id; }

    public String getOwnerId() {
        return ownerId;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Skill> skills) {
        this.skills = skills;
    }

    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    public String getPreviewDescription() {
        return previewDescription;
    }

    public Map<String, Long> getWordStats() { return wordStats; }

    public void setWordStats(Map<String, Long> wordStats) { this.wordStats = wordStats; }
}
