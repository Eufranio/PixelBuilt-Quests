package online.pixelbuilt.pbquests.storage.sql;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import online.pixelbuilt.pbquests.storage.sql.persister.TaskTypePersister;
import online.pixelbuilt.pbquests.task.TaskType;

import java.sql.SQLException;
import java.util.Date;

@DatabaseTable(tableName = "status")
public class QuestStatus extends BaseDaoEnabled<QuestStatus, Integer> {

    @DatabaseField(generatedId = true, index = true)
    public int id;

    @DatabaseField
    public Date started = new Date();

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public PlayerData player;

    @DatabaseField
    public String questLine;

    @DatabaseField
    public int questId;

    @DatabaseField(persisterClass = TaskTypePersister.class)
    public TaskType task;

    @DatabaseField
    public int taskId;

    @DatabaseField
    public int current = 0;

    // TODO: notifications on status update
    public void onUpdate() {
        try {
            this.update();
        } catch (SQLException e) { e.printStackTrace(); }
    }

}
