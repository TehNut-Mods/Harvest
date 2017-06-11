package tehnut.harvest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = Harvest.MODID, name = Harvest.NAME, version = Harvest.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.11,1.13)")
public class Harvest {

    public static final String MODID = "harvest";
    public static final String NAME = "Harvest";
    public static final String VERSION = "@VERSION@";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final Map<BlockStack, Crop> CROP_MAP = new HashMap<BlockStack, Crop>();
    public static final Map<Block, IReplantHandler> CUSTOM_HANDLERS = new HashMap<Block, IReplantHandler>();

    public static final Method _GET_SEED;

    static {
        _GET_SEED = ReflectionHelper.findMethod(BlockCrops.class, null, new String[]{"getSeed", "func_149866_i"});
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        JsonConfigHandler.init(new File(event.getModConfigurationDirectory(), MODID + ".json"));
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onInteract(PlayerInteractEvent.RightClickBlock event) {
            if (event.getHand() != EnumHand.MAIN_HAND)
                return;

            BlockStack worldBlock = BlockStack.getStackFromPos(event.getWorld(), event.getPos());
            if (CROP_MAP.containsKey(worldBlock)) {
                if (CUSTOM_HANDLERS.containsKey(worldBlock.getBlock()))
                    CUSTOM_HANDLERS.get(worldBlock.getBlock()).handlePlant(event.getWorld(), event.getPos(), worldBlock.getState(), event.getEntityPlayer(), event.getWorld().getTileEntity(event.getPos()));
                else
                    IReplantHandler.DEFAULT_HANDLER.handlePlant(event.getWorld(), event.getPos(), worldBlock.getState(), event.getEntityPlayer(), event.getWorld().getTileEntity(event.getPos()));

                event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
                event.setUseItem(Event.Result.DENY);
            }
        }
    }
}
