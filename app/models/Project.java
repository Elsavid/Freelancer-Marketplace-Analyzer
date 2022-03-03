package models;

import java.util.Date;

public class Project {
    private long ownerId;
    private Date timeSubmitted;
    private String title;
    private String type;
    private String skills;

    public Project(long ownerId,Date timeSubmitted,String title,String type,String skills){
        this.ownerId=ownerId;
        this.timeSubmitted=timeSubmitted;
        this.title=title;
        this.type=type;
        this.skills=skills;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public Date getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(Date timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
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

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
