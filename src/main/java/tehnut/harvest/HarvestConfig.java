package tehnut.harvest;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class HarvestConfig {

    private List<Crop> crops;
    private float exhaustionPerHarvest;
    private boolean additionalLogging;
    private boolean firstRunSearch;
    private final transient Map<BlockStack, Crop> cropMap;

    public HarvestConfig(List<Crop> crops, float exhaustionPerHarvest, boolean additionalLogging, boolean firstRunSearch) {
        this.crops = crops;
        this.exhaustionPerHarvest = exhaustionPerHarvest;
        this.additionalLogging = additionalLogging;
        this.firstRunSearch = firstRunSearch;
        this.cropMap = Maps.newHashMap();
    }

    public HarvestConfig() {
        this(JsonConfigHandler.handleDefaults(), 0.005F, false, false);
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

    public boolean shouldLog() {
        return additionalLogging;
    }

    public boolean runFirstStartSearch() {
        return additionalLogging;
    }

    public Map<BlockStack, Crop> getCropMap() {
        return cropMap;
    }
}
