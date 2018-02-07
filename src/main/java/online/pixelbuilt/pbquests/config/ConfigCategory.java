package online.pixelbuilt.pbquests.config;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class ConfigCategory {

    @Setting
    public List<QuestLine> questLines = Lists.newArrayList(new QuestLine());

    @Setting(comment = "ID of the item you want the quest settings item to be")
    public String questSettingsItem = "minecraft:arrow";

    public Quest getQuestFor(Pair<String, Integer> quest) {
        QuestLine questLine = questLines.stream().filter(line -> line.name.equals(quest.getKey())).findFirst().orElse(null);
        if (questLine != null) {
            return questLine.quests.stream().filter(q -> q.questId == quest.getValue()).findFirst().orElse(null);
        }
        return null;
    }

}
