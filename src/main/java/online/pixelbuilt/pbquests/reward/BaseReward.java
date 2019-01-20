package online.pixelbuilt.pbquests.reward;

import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;

/**
 * Created by Frani on 20/01/2019.
 */
public interface BaseReward {

    void execute(Player player, Map<String, String> options, Quest quest, QuestLine line, int questId);

}
