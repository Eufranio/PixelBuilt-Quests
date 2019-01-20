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
public class ProgressReward implements BaseReward {

    @Setting(comment = "1 = set, 2 = add")
    public int defaultProgressMode = 2;

    @Setting
    public int defaultProgressAfter = 1;

    @Override
    public void execute(Player player, Map<String, String> options, Quest quest, QuestLine line, int questId) {
        int progressMode = Integer.parseInt(options.getOrDefault("progressMode", ""+defaultProgressMode));
        int progressAfter = Integer.parseInt(options.getOrDefault("progressAfter", ""+defaultProgressAfter));

        if (progressMode == 1) {
            PixelBuiltQuests.getStorage().setProgress(player.getUniqueId(), line, progressAfter);
        } else if (progressMode == 2) {
            PixelBuiltQuests.getStorage().addProgress(player.getUniqueId(), line, progressAfter);
        }
    }
}
