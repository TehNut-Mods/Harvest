package tehnut.harvest;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class JsonConfigHandler {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().registerTypeAdapter(BlockStack.class, new SerializerBlockStack()).create();
    public static List<Crop> tempList = Lists.newArrayList();

    public static void init(File jsonConfig, File oldConfig) {
        try {
            if (oldConfig.exists()) {
                FileReader reader = new FileReader(oldConfig);
                tempList = gson.fromJson(reader, new TypeToken<List<Crop>>() {}.getType());
                reader.close();
                FileUtils.forceDelete(oldConfig);
            }

            if (!jsonConfig.exists() && jsonConfig.createNewFile()) {
                HarvestConfig config = new HarvestConfig();
                String json = gson.toJson(config);
                FileWriter writer = new FileWriter(jsonConfig);
                writer.write(json);
                writer.close();
            }

            FileReader reader = new FileReader(jsonConfig);
            Harvest.config = gson.fromJson(reader, HarvestConfig.class);
            reader.close();
            Harvest.config.initCropMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Crop> handleDefaults() {
        if (!tempList.isEmpty()) // If old config has been parsed before
            return tempList;

        List<Crop> defaultList = Lists.newArrayList();
        defaultList.add(new Crop(new BlockStack(Blocks.WHEAT, getMaxAge(Blocks.WHEAT)), new BlockStack(Blocks.WHEAT, 0)));
        defaultList.add(new Crop(new BlockStack(Blocks.CARROTS, getMaxAge(Blocks.CARROTS)), new BlockStack(Blocks.CARROTS, 0)));
        defaultList.add(new Crop(new BlockStack(Blocks.POTATOES, getMaxAge(Blocks.POTATOES)), new BlockStack(Blocks.POTATOES, 0)));
        defaultList.add(new Crop(new BlockStack(Blocks.BEETROOTS, getMaxAge(Blocks.BEETROOTS)), new BlockStack(Blocks.BEETROOTS, 0)));
        defaultList.add(new Crop(new BlockStack(Blocks.NETHER_WART, 3), new BlockStack(Blocks.NETHER_WART, 0)));

        return defaultList;
    }

    private static int getMaxAge(Block block) {
        if (block instanceof BlockCrops)
            return ((BlockCrops) block).getMaxAge();

        BlockStateContainer container = block.getBlockState();
        if (container.getProperty("age") != null) {
            IProperty<Integer> ageProp = (IProperty<Integer>) container.getProperty("age");
            int max = 0;
            for (Integer integer : ageProp.getAllowedValues())
                max = Math.max(max, integer);

            return max;
        }

        return -1;
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
