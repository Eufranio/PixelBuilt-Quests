package dev.eufranio.pixelbuiltquests.reward;

import com.google.common.collect.Lists;
import dev.eufranio.pixelbuiltquests.reward.impl.CommandReward;
import dev.eufranio.pixelbuiltquests.reward.impl.MessageReward;
import dev.eufranio.pixelbuiltquests.reward.impl.ProgressReward;
import dev.eufranio.pixelbuiltquests.reward.impl.TeleportReward;

import java.util.List;

public class RewardTypes {

    public static final RewardType TELEPORT = new RewardType("teleport", "Teleport", TeleportReward.class, TeleportReward::new);

    public static final RewardType PROGRESS = new RewardType("progress", "Progress", ProgressReward.class, ProgressReward::new);

    public static final RewardType COMMAND = new RewardType("command", "Commands", CommandReward.class, CommandReward::new);

    public static final RewardType MESSAGE = new RewardType("message", "Messages", MessageReward.class, MessageReward::new);

    public static List<RewardType> defaults() {
        return Lists.newArrayList(
                TELEPORT,
                PROGRESS,
                COMMAND,
                MESSAGE
        );
    }

}
