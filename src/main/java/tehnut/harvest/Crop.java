package tehnut.harvest;

public class Crop {

    private final BlockStack initialBlock;
    private final BlockStack finalBlock;

    public Crop(BlockStack initialBlock, BlockStack finalBlock) {
        this.initialBlock = initialBlock;
        this.finalBlock = finalBlock;
    }

    public BlockStack getInitialBlock() {
        return initialBlock;
    }

    public BlockStack getFinalBlock() {
        return finalBlock;
    }

    @Override
    public String toString() {
        return "Crop{" +
                "initialBlock=" + initialBlock +
                ", finalBlock=" + finalBlock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Crop crop = (Crop) o;

        if (getInitialBlock() != null ? !getInitialBlock().equals(crop.getInitialBlock()) : crop.getInitialBlock() != null)
            return false;
        return getFinalBlock() != null ? getFinalBlock().equals(crop.getFinalBlock()) : crop.getFinalBlock() == null;

    }

    @Override
    public int hashCode() {
        int result = getInitialBlock() != null ? getInitialBlock().hashCode() : 0;
        result = 31 * result + (getFinalBlock() != null ? getFinalBlock().hashCode() : 0);
        return result;
    }
}
