package online.pixelbuilt.pbquests.task;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

/**
 * Created by Frani on 20/01/2019.
 */
@CatalogedBy(TaskTypes.class)
public class TaskType implements CatalogType {

    private String id;
    private String name;
    private Class<? extends BaseTask> task;

    public TaskType(String id, String name, Class<? extends BaseTask> task) {
        this.id = id;
        this.name = name;
        this.task = task;
    }

    @Override
    public String getId() {
        return "pbq:" + this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Class<? extends BaseTask> getCatalogClass() {
        return this.task;
    }
}
