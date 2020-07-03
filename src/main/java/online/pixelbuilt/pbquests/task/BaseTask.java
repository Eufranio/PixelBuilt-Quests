package online.pixelbuilt.pbquests.task;

import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;

import java.util.Map;

/**
 * Created by Frani on 20/01/2019.
 */
public interface BaseTask extends TextRepresentable {

    @Deprecated
    default Text getDisplay() {
        return this.toText();
    }

    TaskType getType();

    int getId();

    boolean isCompleted(PlayerData data, QuestLine line, Quest quest);

    @Deprecated
    default boolean check(Player player, Quest quest, QuestLine line, int questId) {
        return true;
    }

    @Deprecated
    default void complete(Player player, Quest quest, QuestLine line, int questId) {
        //
    }

}
