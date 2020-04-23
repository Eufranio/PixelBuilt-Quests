package online.pixelbuilt.pbquests.task;

import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import org.spongepowered.api.event.Event;

// tasks triggered by an event
public interface TriggeredTask<T extends Event> extends BaseTask, AmountTask {

    @Override
    default void tryIncrease(PlayerData data, QuestStatus status) {
        // only the handle() method sould increase TriggeredTasks
    }

    Class<T> getEventClass();

    void handle(QuestLine line, Quest quest, T event);

}
