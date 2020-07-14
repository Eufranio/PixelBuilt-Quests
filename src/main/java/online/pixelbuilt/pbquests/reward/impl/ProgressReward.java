package online.pixelbuilt.pbquests.reward.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import org.spongepowered.api.entity.living.player.Player;

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
    public void execute(PlayerData data, QuestLine line, Quest quest) {
        if (progressMode == 1) {
            data.setProgress(line, progressAfter);
        } else if (progressMode == 2) {
            data.addProgress(line, progressAfter);
        }
    }
}
