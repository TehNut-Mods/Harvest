package tehnut.harvest;

import net.minecraft.block.Block;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = Harvest.MODID, name = Harvest.NAME, version = Harvest.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.9,1.11)")
public class Harvest {

    public static final String MODID = "Harvest";
    public static final String NAME = "Harvest";
    public static final String VERSION = "@VERSION@";

    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final Boolean IS_DEV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	@Mod.Instance(MODID)
	public static Harvest instance;

    public static final Map<BlockStack, Crop> CROP_MAP = new HashMap<BlockStack, Crop>();
    public static final Map<Block, IReplantHandler> CUSTOM_HANDLERS = new HashMap<Block, IReplantHandler>();

    public static Method getSeed;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        JsonConfigHandler.init(new File(event.getModConfigurationDirectory(), MODID + ".json"));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != EnumHand.MAIN_HAND)
            return;

        BlockStack worldBlock = BlockStack.getStackFromPos(event.getWorld(), event.getPos());
        if (CROP_MAP.containsKey(worldBlock)) {
            if (CUSTOM_HANDLERS.containsKey(worldBlock.getBlock()))
                CUSTOM_HANDLERS.get(worldBlock.getBlock()).handlePlant(event.getWorld(), event.getPos(), worldBlock.getState(), event.getEntityPlayer(), event.getWorld().getTileEntity(event.getPos()));
            else
                IReplantHandler.DEFAULT_HANDLER.handlePlant(event.getWorld(), event.getPos(), worldBlock.getState(), event.getEntityPlayer(), event.getWorld().getTileEntity(event.getPos()));

            event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
        }
    }
}
