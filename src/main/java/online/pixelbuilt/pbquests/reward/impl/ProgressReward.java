package online.pixelbuilt.pbquests.reward.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.reward.BaseReward;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class ProgressReward implements BaseReward<ProgressReward> {

    @Setting(comment = "1 = set, 2 = add")
    public int progressMode = 2;

    @Setting
    public int progressAfter = 1;

    @Override
    public void execute(Player player, Quest quest, QuestLine line, int questId) {
        if (progressMode == 1) {
            PixelBuiltQuests.getStorage().setProgress(player.getUniqueId(), line, progressAfter);
        } else if (progressMode == 2) {
            PixelBuiltQuests.getStorage().addProgress(player.getUniqueId(), line, progressAfter);
        }
    }
}
