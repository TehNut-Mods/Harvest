package tehnut.harvest;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.util.List;

public interface IReplantHandler {
    
    IReplantHandler DEFAULT_HANDLER = new IReplantHandler() {
        @Override
        public void handlePlant(World world, BlockPos pos, IBlockState state, EntityPlayer player, @Nullable TileEntity tileEntity) {
            BlockStack worldBlock = BlockStack.getStackFromPos(world, pos);
            BlockStack newBlock = Harvest.CROP_MAP.get(worldBlock).getFinalBlock();
            List<ItemStack> drops = worldBlock.getBlock().getDrops(world, pos, worldBlock.getState(), 0);
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
                    if (Harvest.getSeed == null) {
                        Harvest.getSeed = BlockCrops.class.getDeclaredMethod(!Harvest.IS_DEV ? "func_149866_i" : "getSeed");
                        Harvest.getSeed.setAccessible(true);
                    }
                    seedNotNull = Harvest.getSeed.invoke(worldBlock.getBlock()) != null;
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
                        world.spawnEntityInWorld(entityItem);
                    }
                }
            }
        }
    };

    void handlePlant(World world, BlockPos pos, IBlockState state, EntityPlayer player, @Nullable TileEntity tileEntity);
}
