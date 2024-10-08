package dev.eufranio.pixelbuiltquests.task.impl;

import dev.architectury.event.EventResult;
import dev.eufranio.pixelbuiltquests.events.internal.PBQInternalEvents;
import dev.eufranio.pixelbuiltquests.listeners.TaskListenerProvider;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.task.TaskTypes;

public class VisitTaskListenerProvider extends TaskListenerProvider {

    final PBQInternalEvents.MoveEntity callback = (entity, world, from, to) -> {
        listeningTasks().forEach(questTask ->
                ((VisitTask) questTask.getTask()).handle(questTask, entity, world, from, to));
        return EventResult.pass();
    };

    @Override
    public void registerListeners() {
        PBQInternalEvents.MOVE_ENTITY.register(callback);
    }

    @Override
    public void unregisterListeners() {
        PBQInternalEvents.MOVE_ENTITY.unregister(callback);
    }

    @Override
    public TaskType taskType() {
        return TaskTypes.VISIT;
    }
}
