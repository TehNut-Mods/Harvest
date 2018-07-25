package tehnut.harvest;

import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;
import org.dimdev.rift.listener.ItemAdder;

import java.io.File;
import java.util.Locale;
import java.util.Map;

public class Harvest implements ItemAdder {

    public static final String MODID = "harvest";
    public static final HarvestConfig CONFIG = JsonUtil.fromJson(TypeToken.get(HarvestConfig.class), new File("config", "harvest.json"), new HarvestConfig());
    public static final Map<Block, IReplantHandler> CUSTOM_HANDLERS = Maps.newHashMap();
    public static final IReplantHandler DEFAULT_HANDLER = (world, pos, state, player, tileEntity) -> {
        IBlockState worldState = world.getBlockState(pos);
        worldState.spawnItems(world, pos, 0);
        world.setBlockState(pos, CONFIG.getCropMap().get(worldState).getFinalBlock());
    };

    @Override
    public void registerItems() {
        CONFIG.initCropMap();

        for (ItemTier tier : ItemTier.values())
            Item.registerItem(new ResourceLocation(MODID, tier.name().toLowerCase(Locale.ROOT) + "_harvester"), new ItemHarvester(tier));
    }
}
