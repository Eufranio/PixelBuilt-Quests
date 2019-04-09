package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class OneTimeTaskReward implements BaseTask<OneTimeTaskReward>, BaseReward<OneTimeTaskReward> {

    @Setting
    public boolean sendMessage = true;

    @Override
    public boolean check(Player player, Quest quest, QuestLine line, int questId) {
        if (PixelBuiltQuests.getStorage().hasRan(player.getUniqueId(), line, questId)) {
            if (sendMessage)
                player.sendMessage(Util.toText(ConfigManager.getConfig().messages.hasRan));
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Quest quest, QuestLine line, int questId) {
        PixelBuiltQuests.getStorage().run(player.getUniqueId(), line, questId);
    }
}
