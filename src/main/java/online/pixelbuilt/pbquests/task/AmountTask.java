package online.pixelbuilt.pbquests.task;

import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;

public interface AmountTask extends BaseTask {

    int getTotal();

    // this should be overriden so it calls increase()
    void tryIncrease(PlayerData data, QuestStatus status);

    default void increase(PlayerData data, QuestStatus status, int amount) {
        status.current += amount;
        if (status.current > getTotal()) {
            status.current = getTotal();
            status.onUpdate();
            return;
        }

        status.onUpdate();
        data.getUser().getPlayer().ifPresent(p -> {
            final Text notifyMessage = this.getNotifyMessage(data, status);
            if (!notifyMessage.isEmpty()) {
                ChatType type = ConfigManager.getConfig().taskNotifyChatType;
                p.sendMessage(type, this.getNotifyMessage(data, status));
            }
        });
    }

    default int getPercentageCompleted(QuestStatus status) {
        double result = (double) status.current / (double) this.getTotal();
        double percent = result * 100;
        return (int) Math.round(percent);
    }

    @Override
    default boolean isCompleted(PlayerData data, QuestLine line, Quest quest) {
        return data.getStatus(this, line, quest)
                .map(s -> s.current >= this.getTotal())
                .orElse(false);
    }

    default Text getNotifyMessage(PlayerData data, QuestStatus status) {
        if (ConfigManager.getConfig().messages.taskNotifyMessage.isEmpty())
            return Text.EMPTY;
        return Util.toText(
                ConfigManager.getConfig().messages.taskNotifyMessage
                    .replace("%display%", Util.fromText(this.toText()))
                    .replace("%task%", this.getType().getName())
                    .replace("%current%", status.current + "")
                    .replace("%total%", this.getTotal() + "")
                    .replace("%percentage%", this.getPercentageCompleted(status) + "")
        );
    }

}
