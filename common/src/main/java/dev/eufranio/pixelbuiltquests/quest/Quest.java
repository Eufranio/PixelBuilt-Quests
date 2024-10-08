package dev.eufranio.pixelbuiltquests.quest;

import com.google.common.collect.Lists;
import dev.eufranio.pixelbuiltquests.quest.executor.QuestExecutor;
import dev.eufranio.pixelbuiltquests.quest.executor.QuestExecutorType;
import dev.eufranio.pixelbuiltquests.quest.executor.QuestExecutorTypes;
import dev.eufranio.pixelbuiltquests.registry.BaseType;
import dev.eufranio.pixelbuiltquests.reward.BaseReward;
import dev.eufranio.pixelbuiltquests.reward.impl.CommandReward;
import dev.eufranio.pixelbuiltquests.reward.impl.MessageReward;
import dev.eufranio.pixelbuiltquests.reward.impl.ProgressReward;
import dev.eufranio.pixelbuiltquests.reward.impl.TeleportReward;
import dev.eufranio.pixelbuiltquests.task.BaseTask;
import dev.eufranio.pixelbuiltquests.task.impl.CostTask;
import dev.eufranio.pixelbuiltquests.task.impl.ItemTask;
import dev.eufranio.pixelbuiltquests.task.impl.PermissionTask;
import dev.eufranio.pixelbuiltquests.task.impl.ProgressRequiredTask;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

@ConfigSerializable
public class Quest implements BaseType {

    @Setting
    @Comment("If this quest should be automatically checked for the player every time the status of any of it's Tasks changes")
    public boolean autocomplete = false;

    @Setting
    public boolean repeatable = true;

    @Setting
    public int timeBetweenMessages = 1;

    @Setting
    private String displayName = "First Quest";

    @Setting
    public List<String> startMessages = Lists.newArrayList("&aYou're starting a Quest, %player%!");

    @Setting
    public List<String> messages = Lists.newArrayList();

    @Setting
    public boolean denyMovement = true;

    @Setting
    public boolean runUponStart = true;

    @Setting
    @Comment("If this quest has a cooldown between executions. If this is true, this quest MUST be repeatable, and" +
            " cooldownDuration must have a valid duration!")
    public boolean cooldown = false;

    @Setting
    public String cooldownDuration = "1m10s";

    @Setting
    private String id = "default_quest";

    @Setting
    @Comment("List of task objects")
    private List<BaseTask> tasks = Lists.newArrayList(
            new CostTask(),
            new ItemTask(),
            new PermissionTask(),
            new ProgressRequiredTask()
    );

    @Setting
    @Comment("List of reward objects")
    public List<BaseReward> rewards = Lists.newArrayList(
            new CommandReward(),
            new MessageReward(),
            new ProgressReward(),
            new TeleportReward()
    );

    @Setting
    public QuestExecutorType executorType = QuestExecutorTypes.DEFAULT;

    public QuestExecutor getExecutor() {
        return this.executorType.instance();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return displayName;
    }

    public List<? extends BaseTask> getTasks() {
        return tasks;
    }

    public List<? extends BaseReward> getRewards() {
        return rewards;
    }
}
