package models;

public class Readability {

    private long fleschIndex;
    private long FKGL;
    private String educationLevel;
    private String contents;

    public Readability(long fleschIndex, long FKGL, String educationLevel, String contents) {
        this.fleschIndex = fleschIndex;
        this.FKGL = FKGL;
        this.educationLevel = educationLevel;
        this.contents = contents;
    }

    public long getFleschIndex() {
        return fleschIndex;
    }

    public void setFleschIndex(int fleschIndex) {
        this.fleschIndex = fleschIndex;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public long getFKGL() {
        return FKGL;
    }

    public void setFKGL(long FKGL) {
        this.FKGL = FKGL;
    }
}
