package tehnut.harvest;

import net.minecraft.block.BlockCrops;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.IPlantable;
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
import java.util.List;
import java.util.Map;

@Mod(modid = Harvest.MODID, name = Harvest.NAME, version = Harvest.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.9,1.10.2)")
public class Harvest {

    public static final String MODID = "Harvest";
    public static final String NAME = "Harvest";
    public static final String VERSION = "@VERSION@";

	@Mod.Instance(MODID)
	public static Harvest instance;

    public static Logger logger = LogManager.getLogger(NAME);

    public Map<BlockStack, Crop> cropMap = new HashMap<BlockStack, Crop>();
    public Method getSeed;

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
        if (cropMap.containsKey(worldBlock)) {
            BlockStack newBlock = cropMap.get(worldBlock).getFinalBlock();
            List<ItemStack> drops = worldBlock.getBlock().getDrops(event.getWorld(), event.getPos(), worldBlock.getState(), 0);
            boolean foundSeed = false;

            for (ItemStack stack : drops) {
                if (stack == null)
                    continue;

                if (stack.getItem() instanceof IPlantable) {
                    if (stack.stackSize > 1)
                        stack.stackSize--;
                    else
                        drops.remove(stack);

                    foundSeed = true;
                    break;
                }
            }

            boolean seedNotNull = true;
            if (worldBlock.getBlock() instanceof BlockCrops) {
                try {
                    if (getSeed == null) {
                        getSeed = BlockCrops.class.getDeclaredMethod("getSeed");
                        getSeed.setAccessible(true);
                    }
                    seedNotNull = getSeed.invoke(worldBlock.getBlock()) != null;
                } catch (Exception e) {
                    logger.error("Failed to reflect BlockCrops: {}", e.getLocalizedMessage());
                }
            }

            if (seedNotNull && foundSeed) {
                if (!event.getWorld().isRemote) {
                    event.getWorld().setBlockState(event.getPos(), newBlock.getState());
                    for (ItemStack stack : drops) {
                        EntityItem entityItem = new EntityItem(event.getWorld(), event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5, stack);
                        entityItem.setPickupDelay(10);
                        event.getWorld().spawnEntityInWorld(entityItem);
                    }
                }
            }

            event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
        }
    }
}
