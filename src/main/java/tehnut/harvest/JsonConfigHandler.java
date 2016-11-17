package tehnut.harvest;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonConfigHandler {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().registerTypeAdapter(BlockStack.class, new SerializerBlockStack()).create();
    public static List<Crop> tempList = new ArrayList<Crop>();

    public static void init(File jsonConfig) {
        try {
            if (!jsonConfig.exists() && jsonConfig.createNewFile()) {
                List<Crop> defaultList = handleDefaults();
                String json = gson.toJson(defaultList, new TypeToken<ArrayList<Crop>>() { }.getType());
                FileWriter writer = new FileWriter(jsonConfig);
                writer.write(json);
                writer.close();
            }

            tempList = gson.fromJson(new FileReader(jsonConfig), new TypeToken<ArrayList<Crop>>() { }.getType());

            for (Crop crop : tempList)
                Harvest.CROP_MAP.put(crop.getInitialBlock(), crop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Crop> handleDefaults() {
        List<Crop> defaultList = new ArrayList<Crop>();
        defaultList.add(new Crop(
                new BlockStack(Blocks.WHEAT, ((BlockCrops)Blocks.WHEAT).getMaxAge()),
                new BlockStack(Blocks.WHEAT, 0)
        ));
        defaultList.add(new Crop(
                new BlockStack(Blocks.CARROTS, ((BlockCrops)Blocks.CARROTS).getMaxAge()),
                new BlockStack(Blocks.CARROTS, 0)
        ));
        defaultList.add(new Crop(
                new BlockStack(Blocks.POTATOES, ((BlockCrops)Blocks.POTATOES).getMaxAge()),
                new BlockStack(Blocks.POTATOES, 0)
        ));
        defaultList.add(new Crop(
                new BlockStack(Blocks.BEETROOTS, ((BlockCrops)Blocks.BEETROOTS).getMaxAge()),
                new BlockStack(Blocks.BEETROOTS, 0)
        ));
        defaultList.add(new Crop(
                new BlockStack(Blocks.NETHER_WART, 3),
                new BlockStack(Blocks.NETHER_WART, 0)
        ));

        return defaultList;
    }

    public static class SerializerBlockStack implements JsonDeserializer<BlockStack>, JsonSerializer<BlockStack> {

        @Override
        public BlockStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsJsonObject().get("blockName").getAsString();
            int meta = 0;
            if (json.getAsJsonObject().get("meta") != null)
                meta = json.getAsJsonObject().get("meta").getAsInt();

            return new BlockStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name)), meta);
        }

        @Override
        public JsonElement serialize(BlockStack src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("blockName", src.getBlock().getRegistryName().toString());
            jsonObject.addProperty("meta", src.getMeta());
            return jsonObject;
        }
    }
}
