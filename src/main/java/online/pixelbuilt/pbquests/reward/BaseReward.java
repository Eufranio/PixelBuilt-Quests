package online.pixelbuilt.pbquests.reward;

import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;

/**
 * Created by Frani on 20/01/2019.
 */
public interface BaseReward<T> {

    void execute(PlayerData data, QuestLine questLine, Quest quest);

}
