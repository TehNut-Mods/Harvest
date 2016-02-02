package tehnut.harvest;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(modid = Harvest.MODID, name = Harvest.NAME, version = Harvest.VERSION)
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
            BlockStack worldBlock = BlockStack.getStackFromPos(event.world, event.x, event.y, event.z);
            if (cropMap.containsKey(worldBlock)) {
                BlockStack newBlock = cropMap.get(worldBlock).getFinalBlock();
                List<ItemStack> drops = worldBlock.getBlock().getDrops(event.world, event.x, event.y, event.z, worldBlock.getMeta(), 0);
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
                    if (!event.world.isRemote) {
                        event.world.setBlock(event.x, event.y, event.z, newBlock.getBlock());
                        event.world.setBlockMetadataWithNotify(event.x, event.y, event.z, newBlock.getMeta(), 3);
                        for (ItemStack stack : drops) {
                            EntityItem entityItem = new EntityItem(event.world, event.x + 0.5, event.y + 0.5, event.z + 0.5, stack);
                            entityItem.delayBeforeCanPickup = 10;
                            event.world.spawnEntityInWorld(entityItem);
                        }
                    }
                }

                event.entityPlayer.swingItem();
            }
        }
    }
}
