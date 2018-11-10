package online.pixelbuilt.pbquests.storage;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.Quest;
import online.pixelbuilt.pbquests.config.QuestLine;
import online.pixelbuilt.pbquests.config.Trigger;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Created by Frani on 10/11/2018.
 */
public abstract class StorageModule {

    public abstract void init(PixelBuiltQuests instance);

    public abstract int getProgress(UUID player, QuestLine line);

    public abstract void setProgress(UUID player, QuestLine line, int progress);

    public abstract Quest getQuest(Entity npc);

    public abstract void addNPC(Entity npc, Quest quest);

    public abstract void removeNPC(Entity npc);

    public abstract boolean hasRan(UUID player, Quest quest);

    public abstract void run(UUID player, Quest quest);

    public abstract Trigger getTriggerAt(Location<World> location);

    public abstract void addTrigger(Trigger trigger);

    public abstract void removeTrigger(Trigger trigger);

    public abstract List<Trigger> getTriggers();

    public abstract List<String> getQuestsRan(UUID player);

    public abstract void resetQuest(UUID player, Quest quest);

}
