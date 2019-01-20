package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
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
@ConfigSerializable
public class ProgressRequiredTask implements BaseTask {

    @Setting(comment = "1 = min, 2 = exact, 3 = max")
    public int defaultProgressCheckMode = 3;

    @Setting
    public int defaultProgressRequired = 0;

    @Override
    public boolean complete(Map<String, String> options, Player player, Quest quest, QuestLine line, int questId) {
        int playerProgress = PixelBuiltQuests.getStorage().getProgress(player.getUniqueId(), line);
        int progressRequired = Integer.parseInt(options.getOrDefault("progressRequired", ""+this.defaultProgressRequired));

        switch (Integer.parseInt(options.getOrDefault("progressCheckMode", ""+this.defaultProgressCheckMode))) {
            case 1:
                if (playerProgress < progressRequired) {
                    player.sendMessage(Util.toText(PixelBuiltQuests.getConfig().messages.noProgressMin
                            .replace("%progress%", ""+progressRequired)
                    ));
                    return false;
                }
                break;
            case 2:
                if (playerProgress != progressRequired) {
                    player.sendMessage(Util.toText(PixelBuiltQuests.getConfig().messages.noProgressExact
                            .replace("%progress%", ""+progressRequired)
                    ));
                    return false;
                }
                break;
            case 3:
                if (playerProgress > progressRequired) {
                    player.sendMessage(Util.toText(PixelBuiltQuests.getConfig().messages.noProgressMax
                            .replace("%progress%", ""+progressRequired)
                    ));
                    return false;
                }
                break;
        }

        return true;
    }
}
