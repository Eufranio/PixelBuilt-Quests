package dev.eufranio.pixelbuiltquests.storage.sql;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

@DatabaseTable(tableName = "players")
public class PlayerData extends BaseDaoEnabled<PlayerData, UUID> {

    // Empty constructor for ORMLite
    public PlayerData() {}

    public PlayerData(UUID id) {
        this.id = id;
        setDao(PixelBuiltQuests.storage().playerDataDao().objDao);
    }

    @DatabaseField(id = true)
    public UUID id;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<TaskStatus> status;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<StartedQuest> startedQuests;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<CompletedQuest> completedQuests;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<QuestLineProgress> progress;

    public Collection<TaskStatus> getStatus() {
        return status;
    }

    public Collection<StartedQuest> getQuestsStarted() {
        return startedQuests;
    }

    public Collection<CompletedQuest> getQuestsCompleted() {
        return completedQuests;
    }

    public Collection<QuestLineProgress> getProgress() {
        return progress;
    }

    public void refreshData() {
        try {
            this.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
