package dev.eufranio.pixelbuiltquests.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import dev.eufranio.pixelbuiltquests.registry.RegistryModule;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class TaskRegistryModule implements RegistryModule<TaskType> {

    final Map<String, TaskType> tasks = Maps.newHashMap();

    @Override
    public void reset() {
        tasks.values().forEach(t -> {
            if (t.getListenerProvider() != null) {
                t.getListenerProvider().unregisterListeners();
            }
        });
        tasks.clear();
    }

    @Override
    public void registerAdditionalCatalog(TaskType extraCatalog) {
        this.tasks.put(extraCatalog.getId(), extraCatalog);
        if (extraCatalog.getListenerProvider() != null) {
            extraCatalog.getListenerProvider().registerListeners();
        }
    }

    @Override
    public Optional<TaskType> getById(String id) {
        String key = id.toLowerCase();
        return Optional.ofNullable(this.tasks.get(key));
    }

    @Override
    public Collection<TaskType> getAll() {
        return ImmutableList.copyOf(this.tasks.values());
    }

    @Override
    public void registerDefaults() {
        for (TaskType task : TaskTypes.defaults()) {
            this.tasks.put(task.getId(), task);
            if (task.getListenerProvider() != null) {
                task.getListenerProvider().registerListeners();
            }
        }
    }

    @Override
    public Class<TaskType> typeClass() {
        return TaskType.class;
    }
}
