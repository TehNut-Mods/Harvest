package tehnut.harvest;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Predicate;

@JsonAdapter(Crop.Adapter.class)
public class Crop implements Predicate<BlockState> {

    private final BlockState mature;
    private Block block;

    public Crop(BlockState mature) {
        this.mature = mature;
    }

    public BlockState getMature() {
        return mature;
    }

    public Block getBlock() {
        return block == null ? block = mature.getBlock() : block;
    }

    @Override
    public boolean test(BlockState state) {
        return state == mature;
    }

    @Override
    public String toString() {
        return "Crop{" + mature.toString() + "}";
    }

    public static class Adapter implements JsonSerializer<Crop>, JsonDeserializer<Crop> {
        @Override
        public Crop deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            Block block = Registry.BLOCKS.get(new Identifier(json.getAsJsonPrimitive("block").getAsString()));
            BlockState state = block.getDefaultState();
            JsonObject stateObject = json.getAsJsonObject("states");
            for (Map.Entry<String, JsonElement> e : stateObject.entrySet()) {
                Property property = block.getStateFactory().getProperty(e.getKey());
                if (property != null) {
                    String valueString = e.getValue().getAsString();
                    Comparable value = (Comparable) property.getValue(valueString).get();
                    state = state.with(property, value);
                }
            }
            return new Crop(state);
        }

        @Override
        public JsonElement serialize(Crop src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("block", Registry.BLOCKS.getId(src.getBlock()).toString());

            String stateString = src.mature.toString();
            String[] properties = stateString.substring(stateString.indexOf("[") + 1, stateString.length() - 1).split(",");

            JsonObject states = new JsonObject();
            for (String property : properties) {
                String[] split = property.split("=");
                states.addProperty(split[0], split[1]);
            }
            object.add("states", states);

            return object;
        }
    }
}
