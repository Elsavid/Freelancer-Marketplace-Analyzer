package models;
/**
 * @author Wenshu Li
 */
public class Readability {

    private long fleschIndex;
    private long FKGL;
    private String educationLevel;
    private String contents;
    /**
     * Parameterized constructor
     *
     * @param fleschIndex                fleschIndex of the project
     * @param FKGL           FKGL of project
     * @param educationLevel         educationLevel of the project
     * @param contents              contents of the project
     */
    public Readability(long fleschIndex, long FKGL, String educationLevel, String contents) {
        this.fleschIndex = fleschIndex;
        this.FKGL = FKGL;
        this.educationLevel = educationLevel;
        this.contents = contents;
    }

    /**
     * Getter for the FleschIndex of the project
     *
     * @return The FleschIndex of this project
     */
    public long getFleschIndex() {
        return fleschIndex;
    }

    /**
     * Getter for the EducationLevel of the project
     *
     * @return The EducationLevel of this project
     */
    public String getEducationLevel() {
        return educationLevel;
    }

    /**
     * Getter for the Contents of the project
     *
     * @return The Contents of this project
     */
    public String getContents() {
        return contents;
    }

    /**
     * Getter for the FKGL of the project
     *
     * @return The FKGL of this project
     */
    public long getFKGL() {
        return FKGL;
    }

}
