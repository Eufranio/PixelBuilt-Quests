package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
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
public class PermissionTask implements BaseTask<PermissionTask> {

    @Setting
    private String permission = "pbq.quest.%line%.%id%";

    @Override
    public boolean check(Player player, Quest quest, QuestLine line, int questId) {
        String perm = permission.replace("%line%", line.getName())
                .replace("%id%", ""+questId);

        if (!player.hasPermission(perm)) {
            player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noPerm));
            return false;
        }
        return true;
    }
}
