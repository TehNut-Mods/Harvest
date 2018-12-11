package tehnut.harvest;

public class HarvestConfig {

    private float exhaustionPerHarvest;

    public HarvestConfig(float exhaustionPerHarvest) {
        this.exhaustionPerHarvest = exhaustionPerHarvest;
    }

    public HarvestConfig() {
        this(0.005F);
    }

    public float getExhaustionPerHarvest() {
        return exhaustionPerHarvest;
    }
}
