package online.pixelbuilt.pbquests.storage.sql;

import com.google.common.collect.Lists;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.BaseTask;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.ArrayList;
import java.util.UUID;

@DatabaseTable(tableName = "playerData")
public class PlayerData extends BaseDaoEnabled<PlayerData, UUID> {

    @DatabaseField(id = true)
    public UUID id;

    @ForeignCollectionField(eager = true)
    public ForeignCollection<QuestStatus> status;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<String> startedQuests = Lists.newArrayList();

    public boolean hasStarted(Quest quest, QuestLine line) {
        return this.startedQuests.contains(line.getName() + "," + quest.getId());
    }

    public int getProgress(BaseTask task, QuestLine questLine, Quest quest) {
        return this.getStatus(task, questLine, quest).current;
    }

    public QuestStatus getStatus(BaseTask task, QuestLine questLine, Quest quest) {
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
