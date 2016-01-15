package tehnut.harvest;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(modid = Harvest.MODID, name = Harvest.NAME, version = Harvest.VERSION, acceptedMinecraftVersions = "[1.8,1.9)")
public class Harvest {

    public static final String MODID = "Harvest";
    public static final String NAME = "Harvest";
    public static final String VERSION = "@VERSION@";

    @Mod.Instance(MODID)
    public static Harvest instance;

    public Map<BlockStack, Crop> cropMap = new HashMap<BlockStack, Crop>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        JsonConfigHandler.init(new File(event.getModConfigurationDirectory(), MODID + ".json"));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {

            BlockStack worldBlock = BlockStack.getStackFromPos(event.world, event.pos);
            if (cropMap.containsKey(worldBlock)) {
                BlockStack newBlock = cropMap.get(worldBlock).getFinalBlock();
                List<ItemStack> drops = worldBlock.getBlock().getDrops(event.world, event.pos, worldBlock.getBlock().getActualState(worldBlock.getState(), event.world, event.pos), 0);
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

                if (foundSeed) {
                    event.world.setBlockState(event.pos, newBlock.getState());
                    if (!event.world.isRemote) {
                        for (ItemStack stack : drops) {
                            EntityItem entityItem = new EntityItem(event.world, event.pos.getX() + 0.5, event.pos.getY() + 0.5, event.pos.getZ() + 0.5, stack);
                            entityItem.setPickupDelay(10);
                            event.world.spawnEntityInWorld(entityItem);
                        }
                    }
                }

                event.entityPlayer.swingItem();
            }
        }
    }
}
