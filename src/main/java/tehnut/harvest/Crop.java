package tehnut.harvest;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Type;

public class Crop {

    @JsonAdapter(BlockStateSerializer.class)
    private final IBlockState initialBlock;
    @JsonAdapter(BlockStateSerializer.class)
    private final IBlockState finalBlock;

    public Crop(IBlockState initialBlock, IBlockState finalBlock) {
        this.initialBlock = initialBlock;
        this.finalBlock = finalBlock;
    }

    public Crop(BlockCrops initial) {
        this(initial.withAge(initial.getMaxAge()), initial.getDefaultState());
    }

    public IBlockState getInitialBlock() {
        return initialBlock;
    }

    public IBlockState getFinalBlock() {
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

    public static class BlockStateSerializer implements JsonDeserializer<IBlockState>, JsonSerializer<IBlockState> {
        @Override
        public IBlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String[] split = json.getAsString().split("\\[");
            split[1] = split[1].substring(0, split[1].lastIndexOf("]")); // Make sure brackets are removed from state

            Block block = Block.REGISTRY.getObject(new ResourceLocation(split[0]));
            if (block == Blocks.AIR)
                return Blocks.AIR.getDefaultState();

            StateContainer<Block, IBlockState> blockState = block.getBlockState();
            IBlockState returnState = blockState.getBaseState();

            // Force our values into the state
            String[] stateValues = split[1].split(","); // Splits up each value
            for (String value : stateValues) {
                String[] valueSplit = value.split("=");
                IProperty property = blockState.getProperty(valueSplit[0]);
                if (property != null)
                    returnState = returnState.withProperty(property, (Comparable) property.parseValue(valueSplit[1]).get());
            }

            return returnState;
        }

        @Override
        public JsonElement serialize(IBlockState src, Type typeOfSrc, JsonSerializationContext context) {
            StringBuilder stateString = new StringBuilder(Block.REGISTRY.getNameForObject(src.getBlock()).toString());
            stateString.append("[");
            boolean firstPass = true;
            for (IProperty property : src.getBlock().getBlockState().getProperties()) {
                stateString.append(firstPass ? "" : ",").append(property.getName()).append("=").append(src.getValue(property));
                firstPass = false;
            }
            stateString.append("]");
            return new JsonPrimitive(stateString.toString());
        }
    }
}
