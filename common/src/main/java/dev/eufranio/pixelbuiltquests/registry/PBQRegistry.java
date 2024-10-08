package dev.eufranio.pixelbuiltquests.registry;

import com.google.common.collect.Maps;
import dev.eufranio.pixelbuiltquests.events.PBQEvents;

import java.util.Collection;
import java.util.Map;

public class PBQRegistry {

    final Map<Class<? extends BaseType>, RegistryModule<? extends BaseType>> registry = Maps.newHashMap();

    public void reset() {
        registry.values().forEach(RegistryModule::reset);
        registry.clear();
    }

    public void loadRegistry() {
        reset();

        PBQEvents.REGISTER_MODULES.invoker()
                .registerModules(this);

        for (RegistryModule module : registry.values()) {
            PBQEvents.REGISTER_DEFAULTS.invoker()
                    .registerDefaults(module.typeClass(), this, module);
        }

        for (RegistryModule module : registry.values()) {
            PBQEvents.REGISTER_TYPES.invoker()
                    .registerTypes(module.typeClass(), this, module);
        }

        for (RegistryModule module : registry.values()) {
            PBQEvents.POST_REGISTER.invoker()
                    .postRegister(module.typeClass(), this, module);
        }
    }

    public <T extends BaseType> void register(Class<T> type, RegistryModule<T> registry) {
        this.registry.put(type, registry);
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseType> void register(Class<T> type, T value) {
        RegistryModule<T> registry = (RegistryModule<T>) this.registry.get(type);
        if (registry != null) {
            registry.registerAdditionalCatalog(value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseType> T get(Class<T> type, String id) {
        RegistryModule<T> registry = (RegistryModule<T>) this.registry.get(type);
        return registry.getAll()
                .stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseType> Collection<T> allOfType(Class<T> type) {
        RegistryModule<T> registry = (RegistryModule<T>) this.registry.get(type);
        return registry.getAll();
    }

}
