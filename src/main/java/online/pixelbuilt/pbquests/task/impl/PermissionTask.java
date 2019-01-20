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
public class PermissionTask implements BaseTask {

    @Setting
    private String defaultPermission = "pbq.quest.%line%.%id%";

    @Override
    public boolean complete(Map<String, String> options, Player player, Quest quest, QuestLine line, int questId) {
        String permission = options.getOrDefault("permission", defaultPermission)
                .replace("%line%", line.getName())
                .replace("%id%", ""+questId);

        if (!player.hasPermission(permission)) {
            player.sendMessage(Util.toText(PixelBuiltQuests.getConfig().messages.noPerm));
            return false;
        }

        return true;
    }
}
