package dev.eufranio.pixelbuiltquests.task;

import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.storage.sql.TaskStatus;

import java.util.UUID;

/// Tasks triggered by an event
public interface TriggeredTask extends BaseTask, AmountTask {

    @Override
    default void tryIncrease(UUID player, TaskStatus status) {
        // only the handle() method sould increase TriggeredTasks
    }

    void handle(QuestReference quest, Object... data);

}
