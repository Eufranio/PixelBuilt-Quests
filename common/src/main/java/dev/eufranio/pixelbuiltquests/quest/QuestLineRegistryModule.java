package dev.eufranio.pixelbuiltquests.quest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.registry.RegistryModule;
import io.github.eufranio.config.Config;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class QuestLineRegistryModule implements RegistryModule<QuestLine> {

    final Map<String, QuestLine> lines = Maps.newHashMap();

    @Override
    public void registerAdditionalCatalog(QuestLine extraCatalog) {
        this.lines.put(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<QuestLine> getById(String id) {
        String key = id.toLowerCase();
        return Optional.ofNullable(this.lines.get(key));
    }

    @Override
    public Collection<QuestLine> getAll() {
        return ImmutableList.copyOf(this.lines.values());
    }

    @Override
    public void registerDefaults() {
        QuestLine dummy = new Config<>(QuestLine.class,
                "default_line.conf",
                ConfigManager.linesDir.toFile(),
                ConfigManager.serializers).get();
        registerAdditionalCatalog(dummy);
    }

    @Override
    public Class<QuestLine> typeClass() {
        return QuestLine.class;
    }
}