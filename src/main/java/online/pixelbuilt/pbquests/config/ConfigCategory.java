package online.pixelbuilt.pbquests.config;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class ConfigCategory {

    @Setting(comment = "The storage system you want to use. 1 = flatfile, 2 = SQL (must fill the database fields). Defaults to flatfile")
    public int storage = 1;

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

        @Setting
        public String noProgressMin = "&cYou need at least %progress% progress to run this quest!";

        @Setting
        public String noProgressExact = "&cYou need to have exactly %progress% progress to run this quest!";

        @Setting
        public String noProgressMax = "&cYou need to have an maximum of %progress% progress to run this quest!";

    }

    @Setting
    public DatabaseCategory database = new DatabaseCategory();

    @ConfigSerializable
    public static class DatabaseCategory {

        @Setting
        public String url = "jdbc:sqlite:PixelBuiltQuests.db";

    }

    public Quest getQuestFor(Pair<String, Integer> quest) {
        if (quest == null) return null;
        QuestLine questLine = questLines.stream().filter(line -> line.name.equals(quest.getKey())).findFirst().orElse(null);
        if (questLine != null) {
            return questLine.quests.stream().filter(q -> q.questId == quest.getValue()).findFirst().orElse(null);
        }
        return null;
    }

    public Quest getQuest(String line, int id) {
        return getQuestFor(new Pair<>(line, id));
    }

    public List<String> getQuests() {
        List<String> list = Lists.newArrayList();
        this.questLines.forEach(line -> line.quests.forEach(q -> {
            list.add(line.name + " " + q.getId());
        }));
        return list;
    }

    public List<String> getQuestLines() {
        return this.questLines.stream()
                .map(QuestLine::getName)
                .collect(Collectors.toList());
    }

    public QuestLine getQuestLine(String name) {
        return this.questLines.stream()
                .filter(q -> q.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
