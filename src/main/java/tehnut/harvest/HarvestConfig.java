package tehnut.harvest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.List;
import java.util.Map;

public class HarvestConfig {

    private List<Crop> crops;
    private float exhaustionPerHarvest;
    private boolean additionalLogging;
    private boolean checkForCrops;
    private final transient Map<IBlockState, Crop> cropMap;

    public HarvestConfig(List<Crop> crops, float exhaustionPerHarvest, boolean additionalLogging, boolean checkForCrops) {
        this.crops = crops;
        this.exhaustionPerHarvest = exhaustionPerHarvest;
        this.additionalLogging = additionalLogging;
        this.checkForCrops = checkForCrops;
        this.cropMap = Maps.newHashMap();
    }

    public HarvestConfig() {
        this(getDefaults(), 0.005F, false, false);
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
        return checkForCrops;
    }

    public Map<IBlockState, Crop> getCropMap() {
        return cropMap;
    }

    public static List<Crop> getDefaults() {
        List<Crop> defaultList = Lists.newArrayList();
        defaultList.add(new Crop((BlockCrops) Blocks.WHEAT));
        defaultList.add(new Crop((BlockCrops) Blocks.CARROTS));
        defaultList.add(new Crop((BlockCrops) Blocks.POTATOES));
        defaultList.add(new Crop((BlockCrops) Blocks.BEETROOTS));
        defaultList.add(new Crop(Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 3), Blocks.NETHER_WART.getDefaultState()));

        return defaultList;
    }
}
