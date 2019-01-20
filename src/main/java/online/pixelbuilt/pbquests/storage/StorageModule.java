package online.pixelbuilt.pbquests.storage;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.config.Trigger;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Created by Frani on 10/11/2018.
 */
public interface StorageModule {

    void init(PixelBuiltQuests instance);

    int getProgress(UUID player, QuestLine line);

    void setProgress(UUID player, QuestLine line, int progress);

    default void addProgress(UUID player, QuestLine line, int progress) {
        this.setProgress(player, line, getProgress(player, line) + progress);
    }

    Quest getQuest(Entity npc);

    void addNPC(Entity npc, Quest quest);

    void removeNPC(Entity npc);

    boolean hasRan(UUID player, Quest quest);

    void run(UUID player, Quest quest);

    Trigger getTriggerAt(Location<World> location);

    void addTrigger(Trigger trigger);

    void removeTrigger(Trigger trigger);

    List<Trigger> getTriggers();

    List<String> getQuestsRan(UUID player);

    void resetQuest(UUID player, Quest quest);

}
