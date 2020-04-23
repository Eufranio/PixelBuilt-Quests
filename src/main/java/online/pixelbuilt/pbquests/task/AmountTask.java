package online.pixelbuilt.pbquests.task;

import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;

public interface AmountTask extends BaseTask {

    int getTotal();

    // this should be overriden so it calls increase()
    void tryIncrease(PlayerData data, QuestStatus status);

    default void increase(PlayerData data, QuestStatus status, int amount) {
        status.current += amount;
        if (status.current > getTotal())
            status.current = getTotal();
        status.onUpdate();
    }

    default int getPercentageCompleted(QuestStatus status) {
        double result = (double) status.current / (double) this.getTotal();
        double percent = result * 100;
        return (int) Math.round(percent);
    }

    @Override
    default boolean isCompleted(PlayerData data, QuestLine line, Quest quest) {
        return data.getProgress(this, line, quest) >= this.getTotal();
    }
}
