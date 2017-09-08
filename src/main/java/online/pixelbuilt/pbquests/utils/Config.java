package online.pixelbuilt.pbquests.utils;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import online.pixelbuilt.pbquests.BlockLocation;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Frani on 05/09/2017.
 */
public class Config {

    private final PixelBuiltQuests instance;
    private final Path configFile;
    private final Path dataFile;
    private ConfigurationNode rootNode;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private TypeToken<Map<String, String>> typeToken;
    private ConfigurationNode dataNode;
    public List<BlockType> blocks;

    public Config(PixelBuiltQuests instance, Path configFile, Path configDir) {
        this.instance = instance;
        this.configFile = configFile;
        this.dataFile = Paths.get(configDir.toString(), "data.nbt");
    }

    public void load() {
        if(!configFile.toFile().exists()) {
            try {
                Sponge.getAssetManager().getAsset(instance, "PBQuests.conf").get().copyToFile(configFile);
            } catch (IOException | NoSuchElementException e) {
                PixelBuiltQuests.logger.error("Could not create the default config! Report it in the plugin issue tracker, and include this stacktrace: ");
                e.printStackTrace();
                return;
            }
        }

        // Loading data
        if(!dataFile.toFile().exists()) {
            try (OutputStream os = Files.newOutputStream(dataFile)) {
                DataFormats.NBT.writeTo(os, new MemoryDataContainer());
            } catch (IOException e) { e.printStackTrace(); }
        }

        try (InputStream is = Files.newInputStream(dataFile)) {
            DataContainer container = DataFormats.NBT.readFrom(is);
            dataNode = DataTranslators.CONFIGURATION_NODE.translate(container);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        loader = HoconConfigurationLoader.builder().setPath(configFile).build();

        try {
            rootNode = loader.load();
        } catch (IOException e) {
            PixelBuiltQuests.logger.error("Could not load the default config! Report it in the plugin issue tracker, and include this stacktrace: ");
            e.printStackTrace();
            return;
        }

        blocks = new ArrayList<>();
        try {
            List<String> typeStrings = rootNode.getNode("triggers").getList(TypeToken.of(String.class));
            for (String type : typeStrings) {
                if (Sponge.getRegistry().getType(BlockType.class, type).isPresent()) {
                    blocks.add(Sponge.getRegistry().getType(BlockType.class, type).get());
                }
            }
        } catch (ObjectMappingException e) {}

        typeToken = new TypeToken<Map<String, String>>() {};

        PixelBuiltQuests.logger.info("Config loaded successfully!");
    }

    public void addTrigger(Location<World> location, String questLine, int questId) {
        try {
            dataNode.getNode("data")
                    .getAppendedNode()
                    .setValue(BlockLocation.type, new BlockLocation(location, questLine, questId));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        Task.builder().execute(() -> {
            DataContainer container = DataTranslators.CONFIGURATION_NODE.translate(dataNode.getNode("data"));
            try (OutputStream os = Files.newOutputStream(dataFile)) {
                DataFormats.NBT.writeTo(os, container);
            } catch (IOException e) { e.printStackTrace(); }
        }).async().submit(PixelBuiltQuests.instance);
    }

    public boolean hasTrigger(Location<World> location) {
        try {
            if (dataNode.getNode("data").getList(TypeToken.of(BlockLocation.class)).stream().anyMatch(loc ->
                    loc.x == location.getBlockX()
                            && loc.y == location.getBlockY()
                            && loc.z == location.getBlockZ()
                            && loc.world.equals(location.getExtent().getUniqueId()))) return true;
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getQuestLine(Location<World> loc) {
        try {
            return dataNode.getNode("data").getList(TypeToken.of(BlockLocation.class)).stream().filter(location ->
                location.x == loc.getBlockX()
             && location.y == loc.getBlockY()
             && location.z == loc.getBlockZ()
             && location.world.toString().equals(loc.getExtent().getUniqueId().toString()))
                    .findFirst()
                    .get()
                    .questLine;
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getQuestId(Location<World> loc) {
        try {
            return dataNode.getNode("data").getList(TypeToken.of(BlockLocation.class)).stream().filter(location ->
                    location.x == loc.getBlockX()
                            && location.y == loc.getBlockY()
                            && location.z == loc.getBlockZ()
                            && location.world.toString().equals(loc.getExtent().getUniqueId().toString()))
                    .findFirst()
                    .get()
                    .questId;
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getString(Object... key) {
        String string = null;
        try {
            string = rootNode.getNode(key).getString();
        } catch (Exception e) {
            PixelBuiltQuests.logger.error("Could not retrieve a specific String!");
            PixelBuiltQuests.logger.error("String key: " + Arrays.toString(key));
            PixelBuiltQuests.logger.error("Stacktrace: " + e);
        }
        return string;
    }

    public ConfigurationNode getQuestNode(String questLine, int questID) {
        return rootNode.getNode("quests", questLine, Integer.toString(questID));
    }
}
