package dev.eufranio.pixelbuiltquests.config;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.config.serialization.BaseRewardSerializer;
import dev.eufranio.pixelbuiltquests.config.serialization.BaseTaskSerializer;
import dev.eufranio.pixelbuiltquests.config.serialization.IdSerializableTypeSerializer;
import dev.eufranio.pixelbuiltquests.config.serialization.GlobalPosSerializer;
import dev.eufranio.pixelbuiltquests.quest.Quest;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import dev.eufranio.pixelbuiltquests.registry.BaseType;
import dev.eufranio.pixelbuiltquests.registry.IdSerializable;
import dev.eufranio.pixelbuiltquests.registry.PBQRegistry;
import dev.eufranio.pixelbuiltquests.reward.BaseReward;
import dev.eufranio.pixelbuiltquests.reward.RewardType;
import dev.eufranio.pixelbuiltquests.task.BaseTask;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import io.github.eufranio.config.Config;
import net.minecraft.core.GlobalPos;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ConfigManager {

    private static Config<ConfigCategory> config;

    public static final Path configDir = PixelBuiltQuests.instance().configDir().toPath();
    public static final Path questsDir = configDir.resolve("quests");
    public static final Path linesDir = configDir.resolve("lines");
    public static final Path rewardsDir = configDir.resolve("rewards");
    public static final Path tasksDir = configDir.resolve("tasks");

    public static final TypeSerializerCollection serializers = TypeSerializerCollection.defaults()
            .childBuilder()
            .register(IdSerializable.class, new IdSerializableTypeSerializer())
            .registerExact(BaseTask.class, new BaseTaskSerializer())
            .registerExact(BaseReward.class, new BaseRewardSerializer())
            .register(GlobalPos.class, new GlobalPosSerializer())
            .build();

    public static void loadConfig() {
        config = new Config<>(ConfigCategory.class,
                "PBQuests.conf",
                configDir.toFile(),
                serializers
        );
    }

    public static void saveRegisteredTasks() {
        PixelBuiltQuests.instance().logger().info("Loading tasks");
        for (TaskType task : PixelBuiltQuests.registry().allOfType(TaskType.class)) {
            new Config<>(task.getValueClass(),
                    task.getId().replace("pbq:", "") + ".conf",
                    tasksDir.toFile(),
                    serializers);
        }
    }

    public static void saveRegisteredRewards() {
        PixelBuiltQuests.instance().logger().info("Loading rewards");
        for (RewardType reward : PixelBuiltQuests.registry().allOfType(RewardType.class)) {
            new Config<>(reward.getValueClass(),
                    reward.getId().replace("pbq:", "") + ".conf",
                    rewardsDir.toFile(),
                    serializers);
        }
    }

    public static void registerQuests(PBQRegistry registry) {
        try {
            PixelBuiltQuests.instance().logger().info("Loading quests");
            if (!Files.exists(questsDir)) questsDir.toFile().mkdirs();
            try (Stream<Path> paths = Files.walk(questsDir)) {
                paths.filter(Files::isRegularFile)
                        .map(path -> loadQuest(path.toFile().getName()))
                        .forEach(quest -> registry.register(Quest.class, quest));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerQuestLines(PBQRegistry registry) {
        try {
            PixelBuiltQuests.instance().logger().info("Loading quest lines");
            if (!Files.exists(linesDir)) linesDir.toFile().mkdirs();
            try (Stream<Path> paths = Files.walk(linesDir)) {
                paths.filter(Files::isRegularFile)
                        .map(path -> loadQuestLine(path.toFile().getName()))
                        .forEach(line -> registry.register(QuestLine.class, line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Quest loadQuest(String fileName) {
        String questId = fileName.replace(".conf", "");

        Config<Quest> data = new Config<>(Quest.class, fileName, questsDir.toFile(), serializers);
        Quest quest = data.get();

        if (!quest.getId().equals(questId)) {
            PixelBuiltQuests.instance().logger().warn("Quest ID mismatch: " + questId + " != " +
                    quest.getId() + ". It is recommended that you change either the file name or " +
                    "the ID in the file so they match. Using " + quest.getId() + " as the ID.");
        }

        return quest;
    }

    static QuestLine loadQuestLine(String fileName) {
        String lineId = fileName.replace(".conf", "");

        Config<QuestLine> data = new Config<>(QuestLine.class, fileName, linesDir.toFile(), serializers);
        QuestLine line = data.get();

        if (!line.getId().equals(lineId)) {
            PixelBuiltQuests.instance().logger().warn("Quest line ID mismatch: " + lineId +
                    " != " + line.getId() + ". It is recommended that you change either the file" +
                    " name or the ID in the file so they match. Using " + line.getId() + " as the " +
                    "ID.");
        }

        return line;
    }

    public static ConfigCategory getConfig() {
        return config.get();
    }
}
