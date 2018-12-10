package tehnut.harvest;

import net.minecraft.block.BlockCrops;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;

public class ReplantHandlers {

    public static final IReplantHandler CONFIG = (world, pos, state, player, tileEntity) -> {
        BlockStack worldBlock = BlockStack.getStackFromPos(world, pos);
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
    };
}
