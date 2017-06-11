package tehnut.harvest;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
            }
        }
    };

    void handlePlant(World world, BlockPos pos, IBlockState state, EntityPlayer player, @Nullable TileEntity tileEntity);
}
