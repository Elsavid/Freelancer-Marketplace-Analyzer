package models;

public class AverageReadability {
    private double fleschIndex;
    private double FKGL;

    public AverageReadability(double fleschIndex, double FKGL) {
        this.fleschIndex = fleschIndex;
        this.FKGL = FKGL;
    }

    public double getFleschIndex() {
        return fleschIndex;
    }

    public double getFKGL() {
        return FKGL;
    }
}
