package dev.eufranio.pixelbuiltquests.task.impl;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.eufranio.pixelbuiltquests.listeners.TaskListenerProvider;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.task.TaskTypes;
import dev.eufranio.pixelbuiltquests.task.TriggeredTask;

public class KillTaskListenerProvider extends TaskListenerProvider {

    final EntityEvent.LivingDeath callback = (entity, source) -> {
        listeningTasks().forEach(questTask ->
                ((TriggeredTask) questTask.getTask()).handle(questTask, entity, source));
        return EventResult.pass();
    };

    @Override
    public TaskType taskType() {
        return TaskTypes.KILL;
    }

    @Override
    public void registerListeners() {
        EntityEvent.LIVING_DEATH.register(callback);
    }

    @Override
    public void unregisterListeners() {
        EntityEvent.LIVING_DEATH.unregister(callback);
    }

}
