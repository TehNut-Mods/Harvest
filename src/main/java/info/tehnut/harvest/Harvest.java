package info.tehnut.harvest;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Harvest implements ModInitializer {

    // Up top against convention so DEFAULT_HANDLER can access it
    public static HarvestConfig config;

    public static final Logger LOGGER = LogManager.getLogger("Harvest");
    public static final IReplantHandler DEFAULT_HANDLER = (world, hit, state, player, tileEntity) -> {
        Crop crop = config.getCrops().stream().filter(c -> c.test(state)).findFirst().orElse(null);
        if (crop == null) {
            debug("No crop found for state {}", state);
            debug("Valid crops {}", Joiner.on(" | ").join(config.getCrops()));
            return ActionResult.PASS;
        }

        BlockPos pos = hit.getBlockPos();
        List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, tileEntity, player, player.getStackInHand(Hand.MAIN_HAND));
        boolean foundSeed = false;
        for (ItemStack drop : drops) {
            Item dropItem = drop.getItem();
            if (dropItem instanceof BlockItem && ((BlockItem)dropItem).getBlock() == state.getBlock()) {
                foundSeed = true;
                drop.decrement(1);
                break;
            }
        }

        if (foundSeed) {
            drops.forEach(stack -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack));
            world.setBlockState(pos, state.getBlock().getDefaultState());
            return ActionResult.SUCCESS;
        }

        debug("Failed to find a seed for {}", state);
        return ActionResult.FAIL;
    };

    @Override
    public void onInitialize() {
        System.out.println(Block.class.getCanonicalName());
        File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "harvest.json");
        try (FileReader reader = new FileReader(configFile)) {
            config = new Gson().fromJson(reader, HarvestConfig.class);
            debug("Successfully loaded config");
            debug("Currently enabled crops: {}", Joiner.on(" | ").join(config.getCrops()));
        } catch (IOException e) {
            config = new HarvestConfig();
            debug("Config not found, generating a new one.");
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
            } catch (IOException e2) {
                debug("Failed to generate new config", e2);
            }
        }

        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!(world instanceof ServerWorld))
                return ActionResult.PASS;

            if (hand != Hand.MAIN_HAND)
                return ActionResult.PASS;

            BlockState state = world.getBlockState(hit.getBlockPos());
            IReplantHandler handler = DEFAULT_HANDLER; // TODO - Allow configuration
            ActionResult result = handler.handlePlant((ServerWorld) world, hit, state, player, world.getBlockEntity(hit.getBlockPos()));
            if (result == ActionResult.SUCCESS) {
                player.swingHand(hand);
                player.addExhaustion(config.getExhaustionPerHarvest());
            }
            debug("Attempted crop harvest with result {} has completed", result);
            return result;
        });
    }

    static void debug(String message, Object... args) {
        if (config.additionalLogging())
            LOGGER.info("[DEBUG] " + message, args);
    }
}
