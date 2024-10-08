package dev.eufranio.pixelbuiltquests.task.impl;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
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
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.UUID;

@ConfigSerializable
public class ProgressRequiredTask implements BaseTask {

    @Setting
    private String id = "4";

    @Setting
    @Comment("MIN, EXACT or MAX")
    private ProgressCheckMode progressCheckMode = ProgressCheckMode.MIN;

    @Setting
    private int progressRequired = 0;

    @Setting
    private boolean sendMessage = true;

    @Override
    public TaskType getType() {
        return TaskTypes.PROGRESS_REQUIRED;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public MutableComponent toText() {
        String progress = switch (progressCheckMode) {
            case MIN -> "Min";
            case EXACT -> "Exactly";
            case MAX -> "Max";
        };
        return Component.empty().withStyle(ChatFormatting.YELLOW)
                .append("Progress (")
                .append(Component.literal(progress + " " + this.progressRequired).withStyle(ChatFormatting.AQUA))
                .append(")");
    }

    @Override
    public boolean isCompleted(UUID player, QuestReference reference) {
        int playerProgress = PixelBuiltQuests.storage().getProgress(player, reference.getQuestLine());
        switch (progressCheckMode) {
            case MIN:
                if (playerProgress < progressRequired) {
                    if (sendMessage)
                        Util.player(player).sendSystemMessage(Util.text(
                                ConfigManager.getConfig().messages.noProgressMin
                                        .replace("%progress%", ""+progressRequired)
                        ));
                    return false;
                }
                break;
            case EXACT:
                if (playerProgress != progressRequired) {
                    if (sendMessage)
                        Util.player(player).sendSystemMessage(Util.text(
                                ConfigManager.getConfig().messages.noProgressExact
                                        .replace("%progress%", ""+progressRequired)
                        ));
                    return false;
                }
                break;
            case MAX:
                if (playerProgress > progressRequired) {
                    if (sendMessage)
                        Util.player(player).sendSystemMessage(Util.text(
                                ConfigManager.getConfig().messages.noProgressMax
                                        .replace("%progress%", ""+progressRequired)
                        ));
                    return false;
                }
                break;
        }
        return true;
    }

    enum ProgressCheckMode {
        MIN,
        EXACT,
        MAX
    }

}
