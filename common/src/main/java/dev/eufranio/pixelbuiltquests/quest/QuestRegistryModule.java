package dev.eufranio.pixelbuiltquests.quest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.registry.RegistryModule;
import io.github.eufranio.config.Config;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class QuestRegistryModule implements RegistryModule<Quest> {

    final Map<String, Quest> quests = Maps.newHashMap();

    @Override
    public void registerAdditionalCatalog(Quest extraCatalog) {
        this.quests.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<Quest> getById(String id) {
        String key = id.toLowerCase();
        return Optional.ofNullable(this.quests.get(key));
    }

    @Override
    public Collection<Quest> getAll() {
        return ImmutableList.copyOf(this.quests.values());
    }

    @Override
    public void registerDefaults() {
        Quest dummy = new Config<>(Quest.class,
                "default_quest.conf",
                ConfigManager.questsDir.toFile(),
                ConfigManager.serializers).get();
        registerAdditionalCatalog(dummy);
    }

    @Override
    public Class<Quest> typeClass() {
        return Quest.class;
    }
}