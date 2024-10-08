package dev.eufranio.pixelbuiltquests.task;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.chat.ChatType;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.quest.Quest;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.storage.sql.TaskStatus;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface AmountTask extends BaseTask {

    int getTotal();

    // this should be overriden and call increase()
    void tryIncrease(UUID player, TaskStatus status);

    default void increase(UUID player, TaskStatus status, int amount) {
        status.setValue(status.getValue() + amount);
        if (status.getValue() > getTotal()) {
            status.setValue(getTotal());
            status.onUpdate();
            return;
        }

        status.onUpdate();

        ServerPlayer p = Util.player(player);
        if (p != null) {
            final MutableComponent notifyMessage = this.getNotifyMessage(player, status);
            if (notifyMessage != null) {
                ChatType type = ConfigManager.getConfig().taskNotifyChatType;
                Util.sendChatTypeMessage(p, type, notifyMessage);
            }

            QuestReference reference = QuestReference.of(status.getQuestLine(), status.getQuestId());
            if (reference.getQuest().autocomplete && PixelBuiltQuests.storage().hasStarted(player, reference)) {
                Quest quest = reference.getQuest();
                quest.getExecutor().execute(p, reference);
            }
        }
    }

    default int getPercentageCompleted(TaskStatus status) {
        double result = (double) status.getValue() / (double) this.getTotal();
        double percent = result * 100;
        return (int) Math.round(percent);
    }

    @Override
    default boolean isCompleted(UUID player, QuestReference reference) {
        return PixelBuiltQuests.storage().getStatus(player, QuestTaskReference.of(reference, this))
                .map(s -> s.getValue() >= this.getTotal())
                .orElse(false);
    }

    default @Nullable MutableComponent getNotifyMessage(UUID player, TaskStatus status) {
        if (ConfigManager.getConfig().messages.taskNotifyMessage.isEmpty())
            return null;
        return Util.text(ConfigManager.getConfig().messages.taskNotifyMessage
                .replace("%display%", this.toText().getString())
                .replace("%task%", this.getType().getName())
                .replace("%current%", status.getValue() + "")
                .replace("%total%", this.getTotal() + "")
                .replace("%percentage%", this.getPercentageCompleted(status) + "")
        );
    }

}
