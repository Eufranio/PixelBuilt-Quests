package dev.eufranio.pixelbuiltquests.task;

import dev.eufranio.pixelbuiltquests.listeners.ListenerProvider;
import dev.eufranio.pixelbuiltquests.utils.WrappedValueType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class TaskType implements WrappedValueType<BaseTask> {

    final String id;
    final String name;
    final Class<? extends BaseTask> task;
    final @Nullable ListenerProvider listenerProvider;

    private TaskType(String id,
                    String name,
                    Class<? extends BaseTask> task,
                    @Nullable ListenerProvider listenerProvider) {
        this.id = id;
        this.name = name;
        this.task = task;
        this.listenerProvider = listenerProvider;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<? extends BaseTask> getValueClass() {
        return this.task;
    }

    public @Nullable ListenerProvider getListenerProvider() {
        return listenerProvider;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private String name;
        private Class<? extends BaseTask> task;
        private @Nullable ListenerProvider listenerProvider;

        public Builder id(String id) {
            if (!id.contains(":")) {
                id = "pbq:" + id;
            }
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder task(Class<? extends BaseTask> task) {
            this.task = task;
            return this;
        }

        public Builder listenerProvider(@Nullable ListenerProvider listenerProvider) {
            this.listenerProvider = listenerProvider;
            return this;
        }

        public TaskType build() {
            return new TaskType(id, name, task, listenerProvider);
        }
    }

}
