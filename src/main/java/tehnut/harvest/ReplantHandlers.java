package tehnut.harvest;

import com.google.common.base.Joiner;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;

public class ReplantHandlers {

    public static final IReplantHandler CONFIG = (world, pos, state, player, tileEntity) -> {
        Crop crop = Harvest.config.getCrops().stream().filter(c -> c.test(state)).findFirst().orElse(null);
        if (crop == null) {
            Harvest.debug("No crop found for state {}", state);
            Harvest.debug("Valid crops {}", Joiner.on(" | ").join(Harvest.config.getCrops()));
            return EnumActionResult.PASS;
        }

        NonNullList<ItemStack> drops = NonNullList.create();
        state.getDrops(drops, world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItem(EnumHand.MAIN_HAND)));
        boolean foundSeed = false;
        for (ItemStack drop : drops) {
            if (drop.getItem().isIn(Harvest.SEED_TAG)) {
                foundSeed = true;
                drop.shrink(1);
                break;
            }
        }

        if (foundSeed) {
            drops.forEach(stack -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
            world.setBlockState(pos, state.getBlock().getDefaultState());
            return EnumActionResult.SUCCESS;
        }

        Harvest.debug("Failed to find a seed for {}", state);
        return EnumActionResult.FAIL;
    };
}
