package dev.eufranio.pixelbuiltquests.quest.executor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import dev.eufranio.pixelbuiltquests.listeners.ListenerProvider;
import dev.eufranio.pixelbuiltquests.registry.RegistryModule;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class QuestExecutorTypeRegistryModule implements RegistryModule<QuestExecutorType> {

    final Map<String, QuestExecutorType> executors = Maps.newHashMap();

    @Override
    public void reset() {
        executors.values().forEach(executor -> {
            if (executor.instance() instanceof ListenerProvider provider) {
                provider.unregisterListeners();
            }
        });
        executors.clear();
    }

    @Override
    public void registerAdditionalCatalog(QuestExecutorType extraCatalog) {
        this.executors.put(extraCatalog.getId(), extraCatalog);
        if (extraCatalog.instance() instanceof ListenerProvider provider) {
            provider.registerListeners();
        }
    }

    @Override
    public Optional<QuestExecutorType> getById(String id) {
        String key = id.toLowerCase();
        if (!key.contains(":")) {
            key = "pbq:" + key;
        }
        return Optional.ofNullable(this.executors.get(key));
    }

    @Override
    public Collection<QuestExecutorType> getAll() {
        return ImmutableList.copyOf(this.executors.values());
    }

    @Override
    public void registerDefaults() {
        for (QuestExecutorType type : QuestExecutorTypes.defaults()) {
            this.executors.put(type.getId(), type);
            if (type.instance() instanceof ListenerProvider provider) {
                provider.registerListeners();
            }
        }
    }

    @Override
    public Class<QuestExecutorType> typeClass() {
        return QuestExecutorType.class;
    }
}
