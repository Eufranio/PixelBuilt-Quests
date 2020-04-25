package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class PermissionTask implements BaseTask {

    @Setting
    public int id = 3;

    @Setting
    private String permission = "pbq.quest.%line%.%id%";

    @Override
    public TaskType getType() {
        return TaskTypes.PERMISSION;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Text getDisplay() {
        return Text.of(TextColors.YELLOW, "Permission");
    }

    @Override
    public boolean isCompleted(PlayerData playerData, QuestLine line, Quest quest) {
        String perm = permission.replace("%line%", line.getName()).replace("%id%", ""+quest.getId());

        if (!playerData.getUser().hasPermission(perm)) {
            playerData.getUser().getPlayer().ifPresent(p ->
                    p.sendMessage(Util.toText(ConfigManager.getConfig().messages.noPerm))
            );
            return false;
        }
        return true;
    }
}
