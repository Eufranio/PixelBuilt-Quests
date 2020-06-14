package online.pixelbuilt.pbquests.storage.sql;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.AmountTask;
import online.pixelbuilt.pbquests.task.BaseTask;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@DatabaseTable(tableName = "playerData")
public class PlayerData extends BaseDaoEnabled<PlayerData, UUID> {

    @DatabaseField(id = true)
    public UUID id;

    @ForeignCollectionField(eager = true)
    public ForeignCollection<QuestStatus> status;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<String> startedQuests = Lists.newArrayList();

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> questsRan = Lists.newArrayList();

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, Integer> progress = Maps.newHashMap();

    public void addQuest(QuestLine line, Quest quest) {
        this.questsRan.add(line.getName() + "," + quest.getId());
    }

    public boolean hasRan(QuestLine line, Quest quest) {
        return this.questsRan.contains(line.getName() + "," + quest.getId());
    }

    public void resetQuestLine(QuestLine line) {
        this.questsRan.removeIf(s -> s.startsWith(line.getName()));
    }

    public void startQuest(QuestLine line, Quest quest) {
        if (!this.startedQuests.contains(line.getName() + "," + quest.getId())) {
            this.startedQuests.add(line.getName() + "," + quest.getId());
        }
    }

    public boolean hasStarted(QuestLine line, Quest quest) {
        return this.startedQuests.contains(line.getName() + "," + quest.getId());
    }

    public int getProgress(QuestLine line) {
        return this.progress.getOrDefault(line.getName(), 0);
    }

    public void addProgress(QuestLine line, int progress) {
        this.progress.merge(line.getName(), progress, Integer::sum);
    }

    public void setProgress(QuestLine line, int progress) {
        this.progress.put(line.getName(), progress);
    }

    public int getProgress(BaseTask task, QuestLine questLine, Quest quest) {
        return this.getStatus(task, questLine, quest).current;
    }

    public QuestStatus getStatus(BaseTask task, QuestLine questLine, Quest quest) {
        if (!(task instanceof AmountTask))
            return null;
        return this.status.stream()
                .filter(s -> s.questLine.equals(questLine.getName()) &&
                        s.questId == quest.getId() &&
                        s.task.getValueClass().equals(task.getClass()) &&
                        s.taskId == task.getId()
                )
                .findFirst()
                .orElseGet(() -> {
                    QuestStatus status = new QuestStatus();
                    status.task = task.getType();
                    status.taskId = task.getId();
                    status.questId = quest.getId();
                    status.questLine = questLine.getName();
                    status.player = this;

                    this.status.add(status);
                    return status;
                });
    }

    public User getUser() {
        return Sponge.getServer().getPlayer(this.id)
                .map(User.class::cast)
                .orElseGet(() -> Sponge.getServiceManager()
                        .provideUnchecked(UserStorageService.class)
                        .get(this.id).orElse(null)
                );
    }
}
