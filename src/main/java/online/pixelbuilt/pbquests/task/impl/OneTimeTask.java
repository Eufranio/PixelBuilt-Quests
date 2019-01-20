package online.pixelbuilt.pbquests.task.impl;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;

/**
 * Created by Frani on 20/01/2019.
 */
public class OneTimeTask implements BaseTask {

    @Override
    public boolean complete(Map<String, String> options, Player player, Quest quest, QuestLine line, int questId) {
        if (PixelBuiltQuests.getStorage().hasRan(player.getUniqueId(), quest)) {
            player.sendMessage(Util.toText(PixelBuiltQuests.getConfig().messages.hasRan));
            return false;
        }

        return true;
    }
}
