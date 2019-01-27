package online.pixelbuilt.pbquests.reward;

import com.google.common.collect.Lists;
import online.pixelbuilt.pbquests.reward.impl.CommandReward;
import online.pixelbuilt.pbquests.reward.impl.MessageReward;
import online.pixelbuilt.pbquests.reward.impl.ProgressReward;
import online.pixelbuilt.pbquests.reward.impl.TeleportReward;
import online.pixelbuilt.pbquests.task.impl.OneTimeTaskReward;

import java.util.List;

/**
 * Created by Frani on 20/01/2019.
 */
public class RewardTypes {

    public static final RewardType TELEPORT = new RewardType("teleport", "Teleport", TeleportReward.class);

    public static final RewardType PROGRESS = new RewardType("progress", "Progress", ProgressReward.class);

    public static final RewardType COMMAND = new RewardType("command", "Commands", CommandReward.class);

    public static final RewardType MESSAGE = new RewardType("message", "Messages", MessageReward.class);

    public static final RewardType ONE_TIME = new RewardType("one_time_reward", "One Time Reward", OneTimeTaskReward.class);

    public static List<RewardType> defaults() {
        return Lists.newArrayList(
                TELEPORT,
                PROGRESS,
                COMMAND,
                MESSAGE,
                ONE_TIME
        );
    }

}
