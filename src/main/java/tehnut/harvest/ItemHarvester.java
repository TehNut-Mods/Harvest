package tehnut.harvest;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTiered;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;

import java.util.Map;

public class ItemHarvester extends ItemTiered {

    public ItemHarvester(IItemTier tier) {
        super(tier, new Builder().group(ItemGroup.TOOLS).maxDamage(tier.func_200926_a()));
    }

    // onItemUse
    @Override
    public EnumActionResult func_195939_a(ItemUseContext context) {
        EntityPlayer player = context.func_195999_j();
        if (player == null)
            return EnumActionResult.PASS;

        IBlockState clicked = context.func_195991_k().getBlockState(context.func_195995_a());
        Map<IBlockState, Crop> cropMap = Harvest.CONFIG.getCropMap();
        Crop crop = Harvest.CONFIG.getCropMap().get(clicked);
        if (crop != null) {
            IReplantHandler replantHandler = Harvest.CUSTOM_HANDLERS.getOrDefault(clicked.getBlock(), Harvest.DEFAULT_HANDLER);
            replantHandler.handlePlant(context.func_195991_k(), context.func_195995_a(), clicked, player, context.func_195991_k().getTileEntity(context.func_195995_a()));
            player.addExhaustion(Harvest.CONFIG.getExhaustionPerHarvest());
            context.func_195996_i().damageItem(1, player);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}
