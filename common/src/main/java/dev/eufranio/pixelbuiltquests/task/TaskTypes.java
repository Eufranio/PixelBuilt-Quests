package dev.eufranio.pixelbuiltquests.task;

import com.google.common.collect.Lists;
import dev.eufranio.pixelbuiltquests.task.impl.*;

import java.util.List;

public final class TaskTypes {

    public static final TaskType COST = TaskType.builder()
            .id("cost")
            .name("Cost")
            .task(CostTask.class)
            .build();

    public static final TaskType PERMISSION = TaskType.builder()
            .id("permission")
            .name("Permission")
            .task(PermissionTask.class)
            .build();

    public static final TaskType PROGRESS_REQUIRED = TaskType.builder()
            .id("progress_required")
            .name("Progress Required")
            .task(ProgressRequiredTask.class)
            .build();

    public static final TaskType ITEM = TaskType.builder()
            .id("item")
            .name("Item")
            .task(ItemTask.class)
            .build();

    public static final TaskType KILL = TaskType.builder()
            .id("kill")
            .name("Kill Mobs")
            .task(KillTask.class)
            .listenerProvider(new KillTaskListenerProvider())
            .build();

    public static final TaskType VISIT = TaskType.builder()
            .id("visit")
            .name("Visit Locations")
            .task(VisitTask.class)
            .listenerProvider(new VisitTaskListenerProvider())
            .build();

    public static List<TaskType> defaults() {
        return Lists.newArrayList(
                COST,
                PERMISSION,
                PROGRESS_REQUIRED,
                ITEM,
                KILL,
                VISIT
        );
    }

}
