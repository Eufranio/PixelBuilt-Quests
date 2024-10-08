package dev.eufranio.pixelbuiltquests.task.impl;

import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.task.BaseTask;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.task.TaskTypes;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.UUID;

@ConfigSerializable
public class PermissionTask implements BaseTask {

    @Setting
    public String id = "3";

    @Setting
    private String permission = "pbq.quest.%line%.%id%";

    @Override
    public TaskType getType() {
        return TaskTypes.PERMISSION;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public MutableComponent toText() {
        return Component.literal("Permission").withStyle(ChatFormatting.YELLOW);
    }

    @Override
    public boolean isCompleted(UUID player, QuestReference reference) {
        String perm = permission
                .replace("%line%", reference.getQuestLine().getId())
                .replace("%id%", reference.getQuest().getId());

        if (!Util.hasPermission(player, perm)) {
            Util.player(player).sendSystemMessage(Util.text(ConfigManager.getConfig().messages.noPerm));
            return false;
        }
        return true;
    }
}
