package dev.eufranio.pixelbuiltquests.trigger;

import com.google.common.collect.Maps;
import dev.eufranio.pixelbuiltquests.registry.RegistryModule;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TriggerTypeRegistryModule implements RegistryModule<TriggerType> {

    final Map<String, TriggerType> types = Maps.newHashMap();

    @Override
    public void reset() {
        types.values().forEach(t -> {
            if (t.getListenerProvider() != null) {
                t.getListenerProvider().unregisterListeners();
            }
        });
    }

    @Override
    public void registerAdditionalCatalog(TriggerType extra) {
        types.put(extra.getId(), extra);
        if (extra.getListenerProvider() != null) {
            extra.getListenerProvider().registerListeners();
        }
    }

    @Override
    public Optional<TriggerType> getById(String id) {
        String key = id.toLowerCase();
        if (!key.contains(":")) {
            key = "pbq:" + key;
        }
        return Optional.ofNullable(types.get(key));
    }

    @Override
    public Collection<TriggerType> getAll() {
        return List.copyOf(types.values());
    }

    @Override
    public void registerDefaults() {
        for (TriggerType type : TriggerTypes.defaults()) {
            types.put(type.getId(), type);
            if (type.getListenerProvider() != null) {
                type.getListenerProvider().registerListeners();
            }
        }
    }

    @Override
    public Class<TriggerType> typeClass() {
        return TriggerType.class;
    }

}
