package dev.eufranio.pixelbuiltquests.storage.sql;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.task.BaseTask;
import dev.eufranio.pixelbuiltquests.task.QuestTaskReference;

import java.sql.SQLException;
import java.util.Date;

@DatabaseTable(tableName = "task_status")
public class TaskStatus extends BaseDaoEnabled<TaskStatus, Integer> {

    // Empty constructor for ORMLite
    public TaskStatus() {}

    public TaskStatus(PlayerData player, QuestTaskReference reference) {
        this.player = player;
        this.questLine = reference.getQuestLine().getId();
        this.questId = reference.getQuest().getId();
        this.taskId = reference.getTask().getId();
        setDao(PixelBuiltQuests.storage().taskStatusDao().objDao);
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(uniqueCombo = true, canBeNull = false, foreign = true, indexName = "PlayerLineIdTaskIdx")
    private PlayerData player;

    @DatabaseField(uniqueCombo = true, canBeNull = false, indexName = "PlayerLineIdTaskIdx")
    private String questLine;

    @DatabaseField(uniqueCombo = true, canBeNull = false, indexName = "PlayerLineIdTaskIdx")
    private String questId;

    @DatabaseField(uniqueCombo = true, canBeNull = false, indexName = "PlayerLineIdTaskIdx")
    private String taskId;

    @DatabaseField(canBeNull = false)
    private Date dateStarted = new Date();

    @DatabaseField
    private int value = 0;

    public String getQuestLine() {
        return questLine;
    }

    public String getQuestId() {
        return questId;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getValue() {
        return value;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setValue(int value) {
        this.value = value;
    }

    // TODO: notifications on status update
    public void onUpdate() {
        try {
            this.update();
        } catch (SQLException e) { e.printStackTrace(); }
    }

}
