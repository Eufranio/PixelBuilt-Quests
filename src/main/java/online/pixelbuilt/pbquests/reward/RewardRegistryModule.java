package online.pixelbuilt.pbquests.reward;

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
public class RewardRegistryModule implements AdditionalCatalogRegistryModule<RewardType> {

    private Map<String, RewardType> rewards = Maps.newHashMap();

    @Override
    public void registerAdditionalCatalog(RewardType extraCatalog) {
        this.rewards.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<RewardType> getById(String id) {
        String key = id.toLowerCase();
        if (!key.contains(":")) {
            key = "pbq:" + key;
        }
        return Optional.ofNullable(this.rewards.get(key));
    }

    @Override
    public Collection<RewardType> getAll() {
        return ImmutableList.copyOf(this.rewards.values());
    }

    @Override
    @DelayedRegistration(RegistrationPhase.PRE_INIT)
    public void registerDefaults() {
        this.rewards.putAll(RewardTypes.defaults().stream()
                .collect(Collectors.toMap(CatalogType::getId, r -> r)));
    }
}