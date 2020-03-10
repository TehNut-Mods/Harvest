package tehnut.harvest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = Harvest.MODID, name = Harvest.NAME, version = Harvest.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.11,1.13)")
public class Harvest {

    public static final String MODID = "harvest";
    public static final String NAME = "Harvest";
    public static final String VERSION = "@VERSION@";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final Map<Block, IReplantHandler> CUSTOM_HANDLERS = new HashMap<>();
    public static final Method _GET_SEED;

    public static HarvestConfig config;

    static {
        _GET_SEED = ReflectionHelper.findMethod(BlockCrops.class, "getSeed", "func_149866_i");
    }

    @Mod.EventHandler
    public void init(FMLPostInitializationEvent event) {
        JsonConfigHandler.init(Loader.instance().getConfigDir());
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onInteract(PlayerInteractEvent.RightClickBlock event) {
            if (event.getHand() != EnumHand.MAIN_HAND)
                return;

            BlockStack worldBlock = BlockStack.getStackFromPos(event.getWorld(), event.getPos());
            if (config.getCropMap().containsKey(worldBlock)) {
                if (CUSTOM_HANDLERS.containsKey(worldBlock.getBlock()))
                    CUSTOM_HANDLERS.get(worldBlock.getBlock()).handlePlant(event.getWorld(), event.getPos(), worldBlock.getState(), event.getEntityPlayer(), event.getWorld().getTileEntity(event.getPos()));
                else
                    defaultHandlePlant(event.getWorld(), event.getPos(), worldBlock.getState(), event.getEntityPlayer(), worldBlock);

                event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
                event.setUseItem(Event.Result.DENY);
                event.getEntityPlayer().addExhaustion(config.getExhaustionPerHarvest());
            }
        }

        private static void defaultHandlePlant(World world, BlockPos pos, IBlockState state, EntityPlayer player, BlockStack worldBlock) {
            BlockStack newBlock = Harvest.config.getCropMap().get(worldBlock).getFinalBlock();
            NonNullList<ItemStack> drops = NonNullList.create();
            worldBlock.getBlock().getDrops(drops, world, pos, state, 0);
            ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, 0, 1.0F, false, player);
            boolean foundSeed = false;

            for (ItemStack stack : drops) {
                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IPlantable) {
                    stack.shrink(1);
                    foundSeed = true;
                    break;
                }
            }

            boolean seedNotNull = true;
            if (worldBlock.getBlock() instanceof BlockCrops) {
                try {
                    Item seed = (Item) Harvest._GET_SEED.invoke(worldBlock.getBlock());
                    seedNotNull = seed != null && seed != Items.AIR;
                } catch (Exception e) {
                    Harvest.LOGGER.error("Failed to reflect BlockCrops: {}", e.getLocalizedMessage());
                }
            }

            if (seedNotNull && foundSeed) {
                if (!world.isRemote) {
                    world.setBlockState(pos, newBlock.getState());
                    for (ItemStack stack : drops) {
                        EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                        entityItem.setPickupDelay(10);
                        world.spawnEntity(entityItem);
                    }
                }
            } else {
                if (Harvest.config.shouldLog())
                    Harvest.LOGGER.info("Did not harvest. seedNotNull - {}, foundSeed - {}", seedNotNull, foundSeed);
            }
        }
    }
}
