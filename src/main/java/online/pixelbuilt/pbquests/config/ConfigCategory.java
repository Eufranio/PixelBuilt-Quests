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

    @Setting
    public Messages messages = new Messages();

    @ConfigSerializable
    public static class Messages {

        @Setting
        public String noQuest = "&c There is no Quest with this quest line/id!";

        @Setting
        public String prefix = "&9&l Quests > ";

        @Setting
        public String noPerm = "&cYou don't have permission to run this quest!";

        @Setting
        public String noLevel = "&cYou need at least level &e%level%&c in the &e%line%&c quest line to run this quest!";

        @Setting
        public String noMoney = "&cYou need at least &e$%money%&c to run this quest!";

        @Setting
        public String noItem = "&cYou need at least one &e%item%&c to run this quest!";

        @Setting
        public String hasRan = "&cThis is a one time quest and you've already completed it!";

        @Setting
        public String finish = "&aCongratulations, you just finished the &e%quest%&c quest!";

    }

    public Quest getQuestFor(Pair<String, Integer> quest) {
        QuestLine questLine = questLines.stream().filter(line -> line.name.equals(quest.getKey())).findFirst().orElse(null);
        if (questLine != null) {
            return questLine.quests.stream().filter(q -> q.questId == quest.getValue()).findFirst().orElse(null);
        }
        return null;
    }

}
