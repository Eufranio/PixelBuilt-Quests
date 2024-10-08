package dev.eufranio.pixelbuiltquests.storage.sql;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;

import java.util.Date;

@DatabaseTable(tableName = "completedQuests")
public class CompletedQuest extends BaseDaoEnabled<CompletedQuest, Integer> {

    // Empty constructor for ORMLite
    public CompletedQuest() {}

    public CompletedQuest(PlayerData player, QuestReference reference, Date dateCompleted) {
        this.player = player;
        this.questLine = reference.getQuestLine().getId();
        this.questId = reference.getQuest().getId();
        this.dateCompleted = dateCompleted;
        setDao(PixelBuiltQuests.storage().completedQuestDao().objDao);
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(uniqueCombo = true, canBeNull = false, foreign = true)
    private PlayerData player;

    @DatabaseField(uniqueCombo = true, canBeNull = false)
    private String questLine;

    @DatabaseField(uniqueCombo = true, canBeNull = false)
    private String questId;

    @DatabaseField(uniqueCombo = true, canBeNull = false)
    private Date dateCompleted;

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public String getQuestLine() {
        return questLine;
    }

    public String getQuestId() {
        return questId;
    }

}
