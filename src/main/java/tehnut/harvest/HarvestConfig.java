package tehnut.harvest;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class HarvestConfig {

    private List<Crop> crops;
    private float exhaustionPerHarvest;
    private final transient Map<BlockStack, Crop> cropMap;

    public HarvestConfig(List<Crop> crops, float exhaustionPerHarvest) {
        this.crops = crops;
        this.exhaustionPerHarvest = exhaustionPerHarvest;
        this.cropMap = Maps.newHashMap();
    }

    public HarvestConfig() {
        this(JsonConfigHandler.handleDefaults(), 0.005F);
    }

    public void initCropMap() {
        for (Crop crop : crops)
            cropMap.put(crop.getInitialBlock(), crop);
    }

    public List<Crop> getCrops() {
        return crops;
    }

    public float getExhaustionPerHarvest() {
        return exhaustionPerHarvest;
    }

    public Map<BlockStack, Crop> getCropMap() {
        return cropMap;
    }
}
