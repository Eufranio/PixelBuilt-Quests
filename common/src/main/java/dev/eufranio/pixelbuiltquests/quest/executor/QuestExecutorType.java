package dev.eufranio.pixelbuiltquests.quest.executor;

import dev.eufranio.pixelbuiltquests.registry.BaseType;
import dev.eufranio.pixelbuiltquests.registry.IdSerializable;

public class QuestExecutorType implements BaseType, IdSerializable {

    final String id;
    final String name;
    final Class<? extends QuestExecutor> executorClass;
    final QuestExecutor instance;

    public QuestExecutorType(String id,
                             String name,
                             Class<? extends QuestExecutor> executorClass,
                             QuestExecutor instance) {
        if (!id.contains(":")) {
            id = "pbq:" + id;
        }

        this.id = id;
        this.name = name;
        this.executorClass = executorClass;
        this.instance = instance;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name;
    }

    public QuestExecutor instance() {
        return instance;
    }

}
