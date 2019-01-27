package online.pixelbuilt.pbquests.quest;

import com.google.common.collect.ImmutableMap;
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
import org.spongepowered.api.item.ItemTypes;

import java.util.*;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class Quest {

    public Quest() {
        this.tasks.put(TaskTypes.COST, ImmutableMap.of("cost", "0"));
        this.tasks.put(TaskTypes.ITEM, ImmutableMap.of("item", ItemTypes.STONE.getId()));
        this.tasks.put(TaskTypes.ONE_TIME, ImmutableMap.of());
        this.tasks.put(TaskTypes.PERMISSION, ImmutableMap.of("permission", "custom.permission"));
        this.tasks.put(TaskTypes.PROGRESS_REQUIRED, ImmutableMap.of("progressRequired", "0", "progressCheckMode", "3"));

        this.rewards.put(RewardTypes.COMMAND, ImmutableMap.of("commands", "give %player% minecraft:stone"));
        this.rewards.put(RewardTypes.MESSAGE, ImmutableMap.of("messages", "&aSuccessfully completed quest!"));
        this.rewards.put(RewardTypes.PROGRESS, ImmutableMap.of("progressMode", "1", "progressAfter", "1"));
        this.rewards.put(RewardTypes.TELEPORT, ImmutableMap.of("location", "0,0,0,world"));
        this.rewards.put(RewardTypes.ONE_TIME, ImmutableMap.of());
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
    public Map<TaskType, Map<String, String>> tasks = Maps.newHashMap();

    @Setting(comment = "reward <-> options mapping")
    public Map<RewardType, Map<String, String>> rewards = Maps.newHashMap();

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

    public Map<TaskType, Map<String, String>> getTasks() {
        return tasks;
    }

    public Map<RewardType, Map<String, String>> getRewards() {
        return rewards;
    }
}
