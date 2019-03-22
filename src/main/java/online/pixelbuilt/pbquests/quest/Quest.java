package online.pixelbuilt.pbquests.quest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutor;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutorType;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutorTypes;
import online.pixelbuilt.pbquests.reward.RewardType;
import online.pixelbuilt.pbquests.reward.RewardTypes;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemTypes;

import java.util.*;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class Quest {

    private static final Map<CatalogType, Map<String, String>> defaults = new HashMap<CatalogType, Map<String, String>>(){{
        put(TaskTypes.COST, ImmutableMap.of("cost", "0"));
        put(TaskTypes.ITEM, ImmutableMap.of("item", ItemTypes.STONE.getId(), "amount", "1"));
        put(TaskTypes.ONE_TIME, ImmutableMap.of());
        put(TaskTypes.PERMISSION, ImmutableMap.of("permission", "custom.permission"));
        put(TaskTypes.PROGRESS_REQUIRED, ImmutableMap.of("progressRequired", "0", "progressCheckMode", "3"));

        put(RewardTypes.COMMAND, ImmutableMap.of("commands", "give %player% minecraft:stone"));
        put(RewardTypes.MESSAGE, ImmutableMap.of("messages", "&aSuccessfully completed quest!"));
        put(RewardTypes.PROGRESS, ImmutableMap.of("progressMode", "1", "progressAfter", "1"));
        put(RewardTypes.TELEPORT, ImmutableMap.of("location", "0,0,0,world"));
        put(RewardTypes.ONE_TIME, ImmutableMap.of());
    }};

    public Quest() {
        defaults.forEach((t, m) -> {
            if (t instanceof TaskType) {
                TaskEntry entry = new TaskEntry();
                entry.type = (TaskType) t;
                entry.options = m;
                this.tasks.add(entry);
            } else {
                RewardEntry entry = new RewardEntry();
                entry.type = (RewardType) t;
                entry.options = m;
                this.rewards.add(entry);
            }
        });
    }

    @Setting
    public int timeBetweenMessages = 1;

    @Setting
    public List<String> messages = new ArrayList<>();

    @Setting
    public boolean denyMovement = true;

    @Setting
    private int id = 0;

    @Setting(comment = "task <-> options mapping")
    public List<TaskEntry> tasks = Lists.newArrayList();

    @ConfigSerializable
    public static class TaskEntry {

        @Setting
        public TaskType type;

        @Setting
        public Map<String, String> options = Maps.newHashMap();

    }

    @Setting(comment = "reward <-> options mapping")
    public List<RewardEntry> rewards = Lists.newArrayList();

    @ConfigSerializable
    public static class RewardEntry {

        @Setting
        public RewardType type;

        @Setting
        public  Map<String, String> options = Maps.newHashMap();

    }

    @Setting
    public QuestExecutorType executorType = QuestExecutorTypes.DEFAULT;

    public QuestExecutor getExecutor() {
        return this.executorType.getExecutor();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
