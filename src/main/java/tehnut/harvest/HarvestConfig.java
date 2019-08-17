package tehnut.harvest;

import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;

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
                new Crop(Blocks.WHEAT.getDefaultState().with(BlockStateProperties.AGE_0_7, 7)),
                new Crop(Blocks.NETHER_WART.getDefaultState().with(BlockStateProperties.AGE_0_3, 3)),
                new Crop(Blocks.CARROTS.getDefaultState().with(BlockStateProperties.AGE_0_7, 7)),
                new Crop(Blocks.POTATOES.getDefaultState().with(BlockStateProperties.AGE_0_7, 7)),
                new Crop(Blocks.BEETROOTS.getDefaultState().with(BlockStateProperties.AGE_0_3, 3))
        );
    }
}