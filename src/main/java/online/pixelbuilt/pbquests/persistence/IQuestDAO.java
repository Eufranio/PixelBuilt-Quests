package online.pixelbuilt.pbquests.persistence;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

/**
 * Created by NeumimTo on 4.11.2017.
 */
public interface IQuestDAO {
    int getProgress(UUID uuid, String questLine);

    void setProgressLevel(Player p, String questLine, int progress);

    String getQuestLineFromNPC(Entity npc);

    int getQuestIdFromNPC(Entity npc);

    void addNpc(String questLine, int questId, Entity npc);

    void removeNpc(Entity npc);
}
