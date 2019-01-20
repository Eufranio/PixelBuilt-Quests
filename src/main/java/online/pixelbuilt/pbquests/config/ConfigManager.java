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

    public static void init() {
        Path questsDir = PixelBuiltQuests.getInstance().configDir.toPath().resolve("quests");

    }

    private static void loadResources() {
        try {
            Path config = PixelBuiltQuests.getInstance().configDir.toPath();

            Path rewards = config.resolve("rewards");
            for (RewardType reward : Sponge.getRegistry().getAllOf(RewardType.class)) {
                mapping.put(reward.getCatalogClass(), new Config<>(reward.getCatalogClass(), reward.getId() + ".conf", rewards.toFile()).get());
            }

            Path tasks = config.resolve("tasks");
            for (TaskType task : Sponge.getRegistry().getAllOf(TaskType.class)) {
                mapping.put(task.getCatalogClass(), new Config<>(task.getCatalogClass(), task.getId() + ".conf", tasks.toFile()).get());
            }

            try (Stream<Path> paths = Files.walk(config.resolve("quests"))) {
                paths.filter(Files::isRegularFile)
                        .map(path -> new Config<>(Quest.class, path.toFile().getName(), config.resolve("quests").toFile()).get())
                        .forEach(quests::add);
            }

            if (quests.isEmpty()) {
                quests.add(new Config<>(Quest.class, "0.conf", config.resolve("quests").toFile()).get());
            }

            Config<QuestLinesCategory> lines = new Config<>(QuestLinesCategory.class, "QuestLines.conf", config.toFile());
            questLines.addAll(lines.get().questLines);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getMapping(Class<? extends T> clazz) {
        return (T) mapping.get(clazz);
    }

}
