package online.pixelbuilt.pbquests.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class ConfigCategory {

    @Setting(comment = "the default chat type used when sending task notify messages, can be either action_bar or chat. " +
            "can be overriden on tasks.")
    public ChatType taskNotifyChatType = ChatTypes.ACTION_BAR;

    @Setting
    public Messages messages = new Messages();

    @ConfigSerializable
    public static class Messages {

        @Setting
        public String playerQuestInfo = "&b%player%&a's Quest Info";

        @Setting
        public String noQuestsStarted = "&cYou have no quests started!";

        @Setting
        public String taskNotCompleted = "&c&lNot Completed";

        @Setting
        public String taskCompleted = "&a&lCompleted";

        @Setting
        public String currentProgress = "&aProgress of &b%player%&a on %line%: &e%progress%";

        @Setting
        public String notAllTasksCompleted = "&cThere are tasks that you haven't completed yet for this Quest: ";

        @Setting
        public String notAllTasksCompletedHint = "&bHint: &7Use /pbq status to check your status on those quests!";

        @Setting
        public String cooldown = "&cYou must wait more &b%cooldown%&c before running this quest again!";

        @Setting(comment = "supports %display%, %task%, %current%, %total% and %percentage%")
        public String taskNotifyMessage = "&aIncreasing %display% &7- &a%current%/%total% &7- &d%percentage%% Completed";

        @Setting
        public String noQuest = "&cThere is no Quest with this quest line/id!";

        @Setting
        public String noPerm = "&cYou don't have permission to run this quest!";

        @Setting
        public String noMoney = "&cYou need at least &e$%money%&c to run this quest!";

        @Setting
        public String hasRan = "&cThis is a one time quest and you've already completed it!";

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
