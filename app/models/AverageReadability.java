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

    public void setFleschIndex(double fleschIndex) {
        this.fleschIndex = fleschIndex;
    }

    public double getFKGL() {
        return FKGL;
    }

    public void setFKGL(double FKGL) {
        this.FKGL = FKGL;
    }
}
