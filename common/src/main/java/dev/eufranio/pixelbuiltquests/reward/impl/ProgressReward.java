package dev.eufranio.pixelbuiltquests.reward.impl;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.reward.BaseReward;
import dev.eufranio.pixelbuiltquests.reward.RewardType;
import dev.eufranio.pixelbuiltquests.reward.RewardTypes;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.UUID;

@ConfigSerializable
public class ProgressReward implements BaseReward {

    @Setting
    @Comment("1 = set, 2 = add")
    private ProgressMode progressMode = ProgressMode.ADD;

    @Setting
    private int progress = 1;

    @Override
    public RewardType getType() {
        return RewardTypes.PROGRESS;
    }

    @Override
    public void execute(UUID player, QuestReference reference) {
        if (progressMode == ProgressMode.SET) {
            PixelBuiltQuests.storage().setProgress(player, reference.getQuestLine(), progress);
        } else if (progressMode == ProgressMode.ADD) {
            PixelBuiltQuests.storage().addProgress(player, reference.getQuestLine(), progress);
        }
    }

    enum ProgressMode {
        SET,
        ADD
    }

}
