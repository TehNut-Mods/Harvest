package tehnut.harvest;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public interface IReplantHandler {
    ActionResult handlePlant(ServerWorld world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity tileEntity);
}
