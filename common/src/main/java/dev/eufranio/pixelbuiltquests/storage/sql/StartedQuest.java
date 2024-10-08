package dev.eufranio.pixelbuiltquests.storage.sql;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;

import java.util.Date;

@DatabaseTable(tableName = "quests_started")
public class StartedQuest extends BaseDaoEnabled<StartedQuest, Integer> {

    // Empty constructor for ORMLite
    public StartedQuest() {}

    public StartedQuest(PlayerData player, QuestReference reference) {
        this.player = player;
        this.questLine = reference.getQuestLine().getId();
        this.questId = reference.getQuest().getId();
        this.dateStarted = new Date();
        setDao(PixelBuiltQuests.storage().startedQuestDao().objDao);
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(uniqueCombo = true, canBeNull = false, foreign = true, indexName = "PlayerLineQuestIdx")
    private PlayerData player;

    @DatabaseField(uniqueCombo = true, canBeNull = false, indexName = "PlayerLineQuestIdx")
    private String questLine;

    @DatabaseField(uniqueCombo = true, canBeNull = false, indexName = "PlayerLineQuestIdx")
    private String questId;

    @DatabaseField(canBeNull = false)
    private Date dateStarted;

    public Date getDateStarted() {
        return dateStarted;
    }

    public String getQuestLine() {
        return questLine;
    }

    public String getQuestId() {
        return questId;
    }

    public QuestReference getReference() {
        return QuestReference.of(questLine, questId);
    }
}
