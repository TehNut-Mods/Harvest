package tehnut.harvest;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IReplantHandler {

    EnumActionResult handlePlant(World world, BlockPos pos, IBlockState state, EntityPlayer player, @Nullable TileEntity tileEntity);
}
