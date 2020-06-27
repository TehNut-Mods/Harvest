package info.tehnut.harvest;

import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;

import java.util.List;

public class HarvestConfig {

    private float exhaustionPerHarvest;
    private boolean additionalLogging;
    private List<Crop> crops;

    public HarvestConfig(float exhaustionPerHarvest, boolean additionalLogging, List<Crop> crops) {
        this.exhaustionPerHarvest = exhaustionPerHarvest;
        this.additionalLogging = additionalLogging;
        this.crops = crops;
    }

    public HarvestConfig() {
        this(0.005F, false, getDefaultCrops());
    }

    public float getExhaustionPerHarvest() {
        return exhaustionPerHarvest;
    }

    public boolean additionalLogging() {
        return additionalLogging;
    }

    public List<Crop> getCrops() {
        return crops;
    }

    private static List<Crop> getDefaultCrops() {
        return Lists.newArrayList(
                new Crop(Blocks.WHEAT.getDefaultState().with(Properties.AGE_7, 7)),
                new Crop(Blocks.NETHER_WART.getDefaultState().with(Properties.AGE_3, 3)),
                new Crop(Blocks.CARROTS.getDefaultState().with(Properties.AGE_7, 7)),
                new Crop(Blocks.POTATOES.getDefaultState().with(Properties.AGE_7, 7)),
                new Crop(Blocks.BEETROOTS.getDefaultState().with(Properties.AGE_3, 3))
        );
    }
}
