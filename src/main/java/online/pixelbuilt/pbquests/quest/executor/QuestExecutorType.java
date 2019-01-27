package online.pixelbuilt.pbquests.quest.executor;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

/**
 * Created by Frani on 23/01/2019.
 */
@CatalogedBy(QuestExecutorTypes.class)
public class QuestExecutorType implements CatalogType {

    private String id;
    private String name;
    private Class<? extends QuestExecutor> executor;

    public QuestExecutorType(String id, String name, Class<? extends QuestExecutor> executor) {
        this.id = id;
        this.name = name;
        this.executor = executor;
    }

    @Override
    public String getId() {
        return "pbq:" + this.id;
    }

    @Override
    public String getName() {
        return name;
    }

    public QuestExecutor getExecutor() {
        try {
            return executor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(executor.getClass() + " doesn't have an empty constructor!");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
