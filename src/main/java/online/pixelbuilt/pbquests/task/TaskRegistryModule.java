package online.pixelbuilt.pbquests.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.util.DelayedRegistration;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Frani on 20/01/2019.
 */
public class TaskRegistryModule implements AdditionalCatalogRegistryModule<TaskType> {

    private Map<String, TaskType> tasks = Maps.newHashMap();

    @Override
    public void registerAdditionalCatalog(TaskType extraCatalog) {
        this.tasks.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<TaskType> getById(String id) {
        String key = id.toLowerCase();
        if (!key.contains(":")) {
            key = "pbq:" + key;
        }
        return Optional.ofNullable(this.tasks.get(key));
    }

    @Override
    public Collection<TaskType> getAll() {
        return ImmutableList.copyOf(this.tasks.values());
    }

    @Override
    @DelayedRegistration(RegistrationPhase.PRE_INIT)
    public void registerDefaults() {
        this.tasks.putAll(TaskTypes.defaults().stream()
                .collect(Collectors.toMap(CatalogType::getId, t -> t)));
    }
}
