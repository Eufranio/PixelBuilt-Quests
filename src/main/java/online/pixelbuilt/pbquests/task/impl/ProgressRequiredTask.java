package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class ProgressRequiredTask implements BaseTask<ProgressRequiredTask> {

    @Setting(comment = "1 = min, 2 = exact, 3 = max")
    public int progressCheckMode = 3;

    @Setting
    public int progressRequired = 0;

    @Setting
    public boolean sendMessage = true;

    @Override
    public boolean check(Player player, Quest quest, QuestLine line, int questId) {
        int playerProgress = PixelBuiltQuests.getStorage().getProgress(player.getUniqueId(), line);
        switch (progressCheckMode) {
            case 1:
                if (playerProgress < progressRequired) {
                    if (sendMessage)
                        player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noProgressMin
                                .replace("%progress%", ""+progressRequired)
                        ));
                    return false;
                }
                break;
            case 2:
                if (playerProgress != progressRequired) {
                    if (sendMessage)
                        player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noProgressExact
                                .replace("%progress%", ""+progressRequired)
                        ));
                    return false;
                }
                break;
            case 3:
                if (playerProgress > progressRequired) {
                    if (sendMessage)
                        player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noProgressMax
                                .replace("%progress%", ""+progressRequired)
                        ));
                    return false;
                }
                break;
        }
        return true;
    }

}
