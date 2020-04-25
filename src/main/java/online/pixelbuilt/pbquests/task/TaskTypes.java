package online.pixelbuilt.pbquests.task;

import com.google.common.collect.Lists;
import online.pixelbuilt.pbquests.task.impl.*;

import java.util.List;

/**
 * Created by Frani on 20/01/2019.
 */
public final class TaskTypes {

    public static final TaskType COST = new TaskType("cost", "Cost", CostTask.class);

    public static final TaskType PERMISSION = new TaskType("permission", "Permission", PermissionTask.class);

    public static final TaskType PROGRESS_REQUIRED = new TaskType("progress_required", "Progress Required", ProgressRequiredTask.class);

    public static final TaskType ITEM = new TaskType("item", "Item", ItemTask.class);

    public static final TaskType KILL = new TaskType("kill", "Kill Mobs", KillTask.class);

    public static final TaskType VISIT = new TaskType("visit", "Visit Locations", VisitTask.class);

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
