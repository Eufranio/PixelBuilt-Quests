package dev.eufranio.pixelbuiltquests.storage.sql;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;

@DatabaseTable(tableName = "progress")
public class QuestLineProgress extends BaseDaoEnabled<QuestLineProgress, Integer> {

    // Empty constructor for ORMLite
    public QuestLineProgress() {}

    public QuestLineProgress(PlayerData player, QuestLine questLine, int progress) {
        this.player = player;
        this.questLine = questLine.getId();
        this.progress = progress;
        setDao(PixelBuiltQuests.storage().questLineProgressDao().objDao);
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(uniqueCombo = true, foreign = true, indexName = "PlayerLineIdx")
    private PlayerData player;

    @DatabaseField(uniqueCombo = true, canBeNull = false, indexName = "PlayerLineIdx")
    private String questLine;

    @DatabaseField(canBeNull = false)
    private int progress = 0;

    public String getQuestLine() {
        return questLine;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
