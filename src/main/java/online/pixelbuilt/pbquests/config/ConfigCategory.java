package online.pixelbuilt.pbquests.config;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class ConfigCategory {

    @Setting(comment = "The storage system you want to use. 1 = flatfile, 2 = SQL (must fill the database fields). Defaults to flatfile")
    public int storage = 1;

    @Setting
    public Messages messages = new Messages();

    @ConfigSerializable
    public static class Messages {

        @Setting
        public String noQuest = "&c There is no Quest with this quest line/id!";

        @Setting
        public String noPerm = "&cYou don't have permission to run this quest!";

        @Setting
        public String noMoney = "&cYou need at least &e$%money%&c to run this quest!";

        @Setting
        public String noItem = "&cYou need at least %amount%x &e%item%&c to run this quest!";

        @Setting
        public String hasRan = "&cThis is a one time quest and you've already completed it!";

        @Setting
        public String finish = "&aCongratulations, you just finished the &e%quest%&a quest!";

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

}
