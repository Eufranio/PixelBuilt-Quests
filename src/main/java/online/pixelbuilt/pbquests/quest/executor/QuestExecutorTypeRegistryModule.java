package online.pixelbuilt.pbquests.quest.executor;

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
 * Created by Frani on 23/01/2019.
 */
public class QuestExecutorTypeRegistryModule implements AdditionalCatalogRegistryModule<QuestExecutorType> {

    private Map<String, QuestExecutorType> executors = Maps.newHashMap();

    @Override
    public void registerAdditionalCatalog(QuestExecutorType extraCatalog) {
        this.executors.put(extraCatalog.getId(), extraCatalog);
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
    @DelayedRegistration(RegistrationPhase.INIT)
    public void registerDefaults() {
        this.executors.putAll(QuestExecutorTypes.defaults().stream()
                .collect(Collectors.toMap(CatalogType::getId, r -> r)));
    }
}
