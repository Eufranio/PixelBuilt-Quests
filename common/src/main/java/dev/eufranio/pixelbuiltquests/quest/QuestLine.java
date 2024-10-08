package dev.eufranio.pixelbuiltquests.quest;

import com.google.common.collect.Lists;
import dev.eufranio.pixelbuiltquests.registry.BaseType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

@ConfigSerializable
public class QuestLine implements BaseType {

    @Setting
    private String id = "default_line";

    @Setting
    private String name = "Default Quest Line";

    @Setting
    private List<String> quests = Lists.newArrayList("default_quest");

    public List<String> getQuests() {
        return quests;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getId() {
        return id;
    }
}
