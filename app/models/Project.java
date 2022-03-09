package models;

import java.util.ArrayList;
import java.util.Date;

public class Project {
    private String ownerId;
    private String submitDate;
    private String title;
    private String type;
    private ArrayList<String> skills;
    private String previewDescription;

    public Project() {
        this.ownerId = "";
        this.submitDate = null;
        this.title = "";
        this.type = "";
        this.skills = new ArrayList<>();
        this.previewDescription = "";
    }

    public Project(String ownerId, String submitDate, String title, String type, ArrayList<String> skills, String previewDescription) {
        this.ownerId = ownerId;
        this.submitDate = submitDate;
        this.title = title;
        this.type = type;
        this.skills = skills;
        this.previewDescription = previewDescription;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }

    public void addSkill(String skill) {
        this.skills.add(skill);
    }

    public String getPreviewDescription() {
        return previewDescription;
    }

    public void setPreviewDescription(String previewDescription) {
        this.previewDescription = previewDescription;
    }
}
