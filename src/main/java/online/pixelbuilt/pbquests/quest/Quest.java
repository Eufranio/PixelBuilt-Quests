package online.pixelbuilt.pbquests.quest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.config.serialization.ValueWrapper;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutor;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutorType;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutorTypes;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.reward.RewardType;
import online.pixelbuilt.pbquests.reward.RewardTypes;
import online.pixelbuilt.pbquests.reward.impl.CommandReward;
import online.pixelbuilt.pbquests.reward.impl.MessageReward;
import online.pixelbuilt.pbquests.reward.impl.ProgressReward;
import online.pixelbuilt.pbquests.reward.impl.TeleportReward;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import online.pixelbuilt.pbquests.task.impl.*;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemTypes;

import java.util.*;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class Quest {

    @Setting
    public int timeBetweenMessages = 1;

    @Setting
    public String displayName = "First Quest";

    @Setting
    public List<String> messages = new ArrayList<>();

    @Setting
    public boolean denyMovement = true;

    @Setting
    private int id = 0;

    @Setting(comment = "task <-> options mapping")
    public List<ValueWrapper<? extends BaseTask>> tasks = Lists.newArrayList(
            new ValueWrapper<>(new CostTask(), TaskTypes.COST),
            new ValueWrapper<>(new ItemTask(), TaskTypes.ITEM),
            new ValueWrapper<>(new OneTimeTaskReward(), TaskTypes.ONE_TIME),
            new ValueWrapper<>(new PermissionTask(), TaskTypes.PERMISSION),
            new ValueWrapper<>(new ProgressRequiredTask(), TaskTypes.PROGRESS_REQUIRED)
    );

    @Setting(comment = "reward <-> options mapping")
    public List<ValueWrapper<? extends BaseReward<?>>> rewards = Lists.newArrayList(
            new ValueWrapper<>(new CommandReward(), RewardTypes.COMMAND),
            new ValueWrapper<>(new MessageReward(), RewardTypes.MESSAGE),
            new ValueWrapper<>(new ProgressReward(), RewardTypes.PROGRESS),
            new ValueWrapper<>(new TeleportReward(), RewardTypes.TELEPORT),
            new ValueWrapper<>(new OneTimeTaskReward(), RewardTypes.ONE_TIME)
    );

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
