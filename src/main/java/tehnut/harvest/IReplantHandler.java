package tehnut.harvest;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public interface IReplantHandler {

    ActionResultType handlePlant(ServerWorld world, BlockPos pos, BlockState state, PlayerEntity player, @Nullable TileEntity tileEntity);
}
