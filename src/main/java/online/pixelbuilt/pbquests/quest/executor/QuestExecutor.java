package online.pixelbuilt.pbquests.quest.executor;

import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Frani on 23/01/2019.
 */
public interface QuestExecutor {

    void execute(Quest quest, QuestLine questLine, int questId, Player player);

}
