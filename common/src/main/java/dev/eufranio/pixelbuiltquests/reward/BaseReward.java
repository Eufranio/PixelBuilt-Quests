package dev.eufranio.pixelbuiltquests.reward;

import dev.eufranio.pixelbuiltquests.quest.QuestReference;

import java.util.UUID;

public interface BaseReward {

    RewardType getType();

    void execute(UUID player, QuestReference reference);

}
