package models;

/**
 * @author Wenshu Li
 */
public class AverageReadability {
    private double fleschIndex;
    private double FKGL;
    /**
     * Parameterized constructor
     *
     * @param fleschIndex                average fleschIndex of the projects
     * @param FKGL           average FKGL of the projects
     */
    public AverageReadability(double fleschIndex, double FKGL) {
        this.fleschIndex = fleschIndex;
        this.FKGL = FKGL;
    }
    /**
     * Getter for the average FleschIndex of the projects
     *
     * @return The average FleschIndex of the projects
     */
    public double getFleschIndex() {
        return fleschIndex;
    }
    /**
     * Getter for the average FKGL of the projects
     *
     * @return The average FleschIndex of the projects
     */
    public double getFKGL() {
        return FKGL;
    }
}
