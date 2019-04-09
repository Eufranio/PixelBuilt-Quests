package online.pixelbuilt.pbquests.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.reward.RewardType;
import online.pixelbuilt.pbquests.task.TaskType;
import org.spongepowered.api.Sponge;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Frani on 20/01/2019.
 */
public class ConfigManager {

    private static Map<Class<?>, Object> mapping = Maps.newHashMap();
    private static List<Quest> quests = Lists.newArrayList();
    private static List<QuestLine> questLines = Lists.newArrayList();

    private static Config<ConfigCategory> config;

    private static Path configDir = PixelBuiltQuests.getInstance().configDir.toPath();
    private static Path questsDir = configDir.resolve("quests");
    private static Path rewardsDir = configDir.resolve("rewards");
    private static Path tasksDir = configDir.resolve("tasks");

    public static void init() {
        config = new Config<>(ConfigCategory.class, "PBQuests.conf", configDir);
        loadResources();
    }

    public static void reload() {
        mapping.clear();
        quests.clear();
        questLines.clear();
        loadResources();
        loadCatalogs();
    }

    private static void loadResources() {
        Config<QuestLinesCategory> lines = new Config<>(QuestLinesCategory.class, "QuestLines.conf", configDir);
        questLines.addAll(lines.get().questLines);
        config = new Config<>(ConfigCategory.class, "PBQuests.conf", configDir);
    }

    public static void loadCatalogs() {
        try {
            for (RewardType reward : Sponge.getRegistry().getAllOf(RewardType.class)) {
                new Config<>(reward.getValueClass(), reward.getId().replace("pbq:", "") + ".conf", rewardsDir);
            }

            for (TaskType task : Sponge.getRegistry().getAllOf(TaskType.class)) {
                new Config<>(task.getValueClass(), task.getId().replace("pbq:", "") + ".conf", tasksDir);
            }

            if (!Files.exists(questsDir)) questsDir.toFile().mkdirs();
            try (Stream<Path> paths = Files.walk(questsDir)) {
                paths.filter(Files::isRegularFile)
                        .map(path -> {
                            Quest quest = new Config<>(Quest.class, path.toFile().getName(), questsDir).get();
                            quest.setId(Integer.parseInt(path.toFile().getName().replace(".conf", "")));
                            return quest;
                        })
                        .forEach(quests::add);
            }

            if (quests.isEmpty()) {
                quests.add(new Config<>(Quest.class, "0.conf", questsDir).get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Quest getQuest(int questId) {
        return quests.stream().filter(q -> q.getId() == questId).findFirst().orElse(null);
    }

    public static List<Quest> getQuests() {
        return quests;
    }

    public static QuestLine getLine(String name) {
        return questLines.stream().filter(q -> q.getName().equals(name)).findFirst().orElse(null);
    }

    public static List<QuestLine> getLines() {
        return questLines;
    }

    public static ConfigCategory getConfig() {
        return config.get();
    }
}
