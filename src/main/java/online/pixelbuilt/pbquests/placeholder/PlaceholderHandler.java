package online.pixelbuilt.pbquests.placeholder;

import com.google.common.reflect.TypeToken;
import me.rojo8399.placeholderapi.*;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.config.serialization.ValueWrapper;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.AmountTask;
import online.pixelbuilt.pbquests.task.BaseTask;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;

public class PlaceholderHandler {

    PixelBuiltQuests plugin;

    public PlaceholderHandler(PixelBuiltQuests plugin) {
        this.plugin = plugin;
    }

    public void init() {
        PlaceholderService service = Sponge.getServiceManager().provideUnchecked(PlaceholderService.class);
        service.loadAll(this, plugin).stream()
                .map(builder -> builder.author("Eufranio").version("1.0"))
                .forEach(builder -> {
                    try {
                        builder.buildAndRegister();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
        service.registerTypeDeserializer(new TypeToken<QuestLine>(){}, ConfigManager::getLine);
        service.registerTypeDeserializer(new TypeToken<Quest>(){}, string -> ConfigManager.getQuest(Integer.parseInt(string)));
        service.registerTypeDeserializer(new TypeToken<QuestLineQuest>(){}, string -> {
            String[] arr = string.split("_");
            if (arr.length != 2)
                return null;
            return new QuestLineQuest() {{
                questLine = ConfigManager.getLine(arr[0]);
                quest = ConfigManager.getQuest(Integer.parseInt(arr[1]));
            }};
        });
        service.registerTypeDeserializer(new TypeToken<QuestLineQuestTaskId>(){}, string -> {
            String[] arr = string.split("_");
            if (arr.length != 3)
                return null;
            return new QuestLineQuestTaskId() {{
                questLine = ConfigManager.getLine(arr[0]);
                quest = ConfigManager.getQuest(Integer.parseInt(arr[1]));
                taskId = Integer.parseInt(arr[2]);
            }};
        });
        service.registerTypeDeserializer(new TypeToken<QuestTaskId>(){}, string -> {
            String[] arr = string.split("_");
            if (arr.length != 2)
                return null;
            return new QuestTaskId() {{
                quest = ConfigManager.getQuest(Integer.parseInt(arr[0]));
                taskId = Integer.parseInt(arr[1]);
            }};
        });
    }

    @Placeholder(id = "pbqtaskneeded")
    public Integer taskNeeded(
            @Source User user,
            @Token QuestLineQuestTaskId data
    ) {
        QuestLine questLine = data.questLine;
        Quest quest = data.quest;
        int taskId = data.taskId;

        BaseTask task = quest.getTask(taskId);
        if (!(task instanceof AmountTask))
            return null;

        AmountTask amountTask = (AmountTask) task;
        PlayerData playerData = PixelBuiltQuests.getStorage().getData(user.getUniqueId());
        QuestStatus status = playerData.getStatus(amountTask, questLine, quest).orElse(null);

        int current = status != null ? status.current : 0;
        return amountTask.getTotal() - current;
    }

    @Placeholder(id = "pbqtaskcurrent")
    public Integer taskCurrent(
            @Source User user,
            @Token QuestLineQuestTaskId data
    ) {
        QuestLine questLine = data.questLine;
        Quest quest = data.quest;
        int taskId = data.taskId;

        BaseTask task = quest.getTask(taskId);
        if (!(task instanceof AmountTask))
            return null;

        AmountTask amountTask = (AmountTask) task;
        PlayerData playerData = PixelBuiltQuests.getStorage().getData(user.getUniqueId());
        QuestStatus status = playerData.getStatus(amountTask, questLine, quest).orElse(null);

        return status != null ? status.current : 0;
    }

    @Placeholder(id = "pbqtaskpercentage")
    public String taskPercentage(
            @Source User user,
            @Token QuestLineQuestTaskId data
    ) {
        QuestLine questLine = data.questLine;
        Quest quest = data.quest;
        int taskId = data.taskId;

        BaseTask task = quest.getTask(taskId);
        if (!(task instanceof AmountTask))
            return null;

        AmountTask amountTask = (AmountTask) task;
        PlayerData playerData = PixelBuiltQuests.getStorage().getData(user.getUniqueId());
        QuestStatus status = playerData.getStatus(amountTask, questLine, quest).orElse(null);

        if (status == null)
            return null;

        return amountTask.getPercentageCompleted(status) + "%";
    }

    @Placeholder(id = "pbqtasktotal")
    public Integer taskTotal(
            @Token QuestTaskId data
    ) {
        Quest quest = data.quest;
        int taskId = data.taskId;

        BaseTask task = quest.getTask(taskId);
        if (!(task instanceof AmountTask))
            return null;

        AmountTask amountTask = (AmountTask) task;
        return amountTask.getTotal();
    }

    @Placeholder(id = "pbqquestpercentage")
    public String questPercentage(
            @Source User user,
            @Token QuestLineQuest data
    ) {
        QuestLine questLine = data.questLine;
        Quest quest = data.quest;

        PlayerData playerData = PixelBuiltQuests.getStorage().getData(user.getUniqueId());
        int completed = (int) quest.tasks.stream()
                .map(ValueWrapper::getValue)
                .filter(task -> task.isCompleted(playerData, questLine, quest))
                .count();

        double result = (double) completed/ (double) quest.tasks.size();
        double percent = result * 100;
        return ((int) Math.round(percent)) + "%";
    }

    @Placeholder(id = "pbqquestlinetotal")
    public Integer questLineTotal(@Token QuestLine questLine) {
        return questLine.quests.size();
    }

    @Placeholder(id = "pbqquestlinecompleted")
    public Integer questLineCompleted(
            @Source User user,
            @Token QuestLine questLine
    ) {
        PlayerData playerData = PixelBuiltQuests.getStorage().getData(user.getUniqueId());
        int completed = 0;

        for (int questId : questLine.quests) {
            Quest quest = ConfigManager.getQuest(questId);
            if (quest == null)
                continue;
            if (playerData.hasRan(questLine, quest))
                completed++;
        }

        return completed;
    }

    @Placeholder(id = "pbqquestlineprogress")
    public Integer questLineProgress(
            @Source User user,
            @Token QuestLine questLine
    ) {
        PlayerData playerData = PixelBuiltQuests.getStorage().getData(user.getUniqueId());
        return playerData.getProgress(questLine);
    }

    public static class QuestLineQuest {
        QuestLine questLine;
        Quest quest;
    }

    public static class QuestTaskId {
        Quest quest;
        int taskId;
    }

    public static class QuestLineQuestTaskId {
        QuestLine questLine;
        Quest quest;
        int taskId;
    }

}
