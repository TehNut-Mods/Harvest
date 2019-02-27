package tehnut.harvest;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Mod(Harvest.MODID)
public class Harvest {

    public static final String MODID = "harvest";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final Tag<Item> SEED_TAG = new ItemTags.Wrapper(new ResourceLocation("harvest", "seeds"));
    public static final Map<Block, IReplantHandler> CUSTOM_HANDLERS = new HashMap<Block, IReplantHandler>();

    public static HarvestConfig config;

    public Harvest() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, EventHandler::onInteract);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event) {
        File configFile = new File(FMLPaths.CONFIGDIR.get().toFile(), "harvest.json");
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
    }

    public static class EventHandler {
        public static void onInteract(PlayerInteractEvent.RightClickBlock event) {
            if (!(event.getWorld() instanceof WorldServer))
                return;

            if (event.getHand() != EnumHand.MAIN_HAND)
                return;

            IBlockState state = event.getWorld().getBlockState(event.getPos());
            IReplantHandler handler = CUSTOM_HANDLERS.getOrDefault(state.getBlock(), ReplantHandlers.CONFIG);
            EnumActionResult result = handler.handlePlant(event.getWorld(), event.getPos(), state, event.getEntityPlayer(), event.getWorld().getTileEntity(event.getPos()));
            if (result == EnumActionResult.SUCCESS) {
                event.getEntityPlayer().swingArm(event.getHand());
                event.getEntityPlayer().addExhaustion(config.getExhaustionPerHarvest());
            }
            debug("Attempted crop harvest with result {} has completed", result);
            event.setUseItem(result == EnumActionResult.SUCCESS ? Event.Result.DENY : Event.Result.ALLOW);
        }
    }

    static void debug(String message, Object... args) {
        if (config.additionalLogging())
            LOGGER.info("[DEBUG] " + message, args);
    }
}
