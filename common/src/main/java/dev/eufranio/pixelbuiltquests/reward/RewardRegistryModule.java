package dev.eufranio.pixelbuiltquests.reward;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import dev.eufranio.pixelbuiltquests.registry.BaseType;
import dev.eufranio.pixelbuiltquests.registry.RegistryModule;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RewardRegistryModule implements RegistryModule<RewardType> {

    final Map<String, RewardType> rewards = Maps.newHashMap();

    @Override
    public void registerAdditionalCatalog(RewardType extraCatalog) {
        this.rewards.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<RewardType> getById(String id) {
        String key = id.toLowerCase();
        return Optional.ofNullable(this.rewards.get(key));
    }

    @Override
    public Collection<RewardType> getAll() {
        return ImmutableList.copyOf(this.rewards.values());
    }

    @Override
    public void registerDefaults() {
        this.rewards.putAll(RewardTypes.defaults().stream()
                .collect(Collectors.toMap(BaseType::getId, r -> r)));
    }

    @Override
    public Class<RewardType> typeClass() {
        return RewardType.class;
    }
}