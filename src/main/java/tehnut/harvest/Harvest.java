package tehnut.harvest;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.fabricmc.fabric.tags.TagRegistry;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Harvest implements ModInitializer {

    public static final Tag<Item> SEED_TAG = TagRegistry.item(new Identifier("harvest", "seeds"));
    public static final Tag<Block> CROP_TAG = TagRegistry.block(new Identifier("harvest", "crops"));
    public static final Logger LOGGER = LogManager.getLogger("Harvest");
    public static final Set<BlockState> FULLY_GROWN = Sets.newHashSet();
    public static final IReplantHandler DEFAULT_HANDLER = (world, pos, state, player, tileEntity) -> {
        if (!CROP_TAG.contains(state.getBlock()))
            return ActionResult.PASS;

        if (!FULLY_GROWN.contains(state))
            return ActionResult.PASS;

        List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, tileEntity, player, player.getStackInHand(Hand.MAIN));
        boolean foundSeed = false;
        for (ItemStack drop : drops) {
            if (SEED_TAG.contains(drop.getItem())) {
                foundSeed = true;
                drop.subtractAmount(1);
                break;
            }
        }
        if (foundSeed) {
            drops.forEach(stack -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack));
            world.setBlockState(pos, state.getBlock().getDefaultState());
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAILURE;
    };

    public static HarvestConfig config;

    @Override
    public void onInitialize() {
        File configFile = new File(FabricLoader.INSTANCE.getConfigDirectory(), "harvest.json");
        try (FileReader reader = new FileReader(configFile)) {
            config = new Gson().fromJson(reader, HarvestConfig.class);
        } catch (IOException e) {
            config = new HarvestConfig();
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
            } catch (IOException e2) {
                // no-op
            }
        }

        PlayerInteractionEvent.INTERACT_BLOCK.register((player, world, hand, pos, facing, hitX, hitY, hitZ) -> {
            if (!(world instanceof ServerWorld))
                return ActionResult.PASS;

            if (hand != Hand.MAIN)
                return ActionResult.PASS;

            BlockState state = world.getBlockState(pos);
            IReplantHandler handler = DEFAULT_HANDLER; // TODO - Allow configuration
            ActionResult result = handler.handlePlant((ServerWorld) world, pos, state, player, world.getBlockEntity(pos));
            if (result == ActionResult.SUCCESS) {
                player.swingHand(hand);
                player.addExhaustion(config.getExhaustionPerHarvest());
            }
            return result;
        });

        // TODO - Not do it like this
        FULLY_GROWN.add(Blocks.WHEAT.getDefaultState().with(Properties.AGE_7, 7));
        FULLY_GROWN.add(Blocks.NETHER_WART.getDefaultState().with(Properties.AGE_3, 3));
        FULLY_GROWN.add(Blocks.CARROTS.getDefaultState().with(Properties.AGE_7, 7));
        FULLY_GROWN.add(Blocks.POTATOES.getDefaultState().with(Properties.AGE_7, 7));
        FULLY_GROWN.add(Blocks.BEETROOTS.getDefaultState().with(Properties.AGE_3, 3));
    }
}
