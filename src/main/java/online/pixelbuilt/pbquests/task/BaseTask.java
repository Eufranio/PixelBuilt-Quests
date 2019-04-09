package online.pixelbuilt.pbquests.task;

import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;

/**
 * Created by Frani on 20/01/2019.
 */
public interface BaseTask<T> {

    boolean check(Player player, Quest quest, QuestLine line, int questId);

    default void complete(Player player, Quest quest, QuestLine line, int questId) {
        //
    }

}
