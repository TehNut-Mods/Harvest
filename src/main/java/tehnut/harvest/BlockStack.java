package tehnut.harvest;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockStack {

    private final Block block;
    private final int meta;

    public BlockStack(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public BlockStack(Block block) {
        this(block, 0);
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return GameData.getBlockRegistry().getNameForObject(getBlock()) + ":" + getMeta();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockStack that = (BlockStack) o;

        if (getMeta() != that.getMeta()) return false;
        return getBlock() != null ? getBlock().equals(that.getBlock()) : that.getBlock() == null;

    }

    @Override
    public int hashCode() {
        int result = getBlock() != null ? getBlock().hashCode() : 0;
        result = 31 * result + getMeta();
        return result;
    }

    public static BlockStack getStackFromPos(World world, int x, int y, int z) {
        return new BlockStack(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
    }
}
