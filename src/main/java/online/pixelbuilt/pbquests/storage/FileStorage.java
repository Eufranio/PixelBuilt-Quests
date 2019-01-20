package online.pixelbuilt.pbquests.storage;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.*;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Created by Frani on 10/11/2018.
 */
public class FileStorage implements StorageModule {

    private Config<DatabaseCategory> dbConfig = new Config<>(DatabaseCategory.class, "Database.conf");
    private Config<TriggersCategory> triggersConfig = new Config<>(TriggersCategory.class, "Triggers.conf");

    @Override
    public void init(PixelBuiltQuests instance) {
        //
    }

    @Override
    public int getProgress(UUID player, QuestLine line) {
        return this.dbConfig.get().getProgress(player, line.getName());
    }

    @Override
    public void setProgress(UUID player, QuestLine line, int progress) {
        this.dbConfig.get().setProgress(player, line.getName(), progress);
        this.dbConfig.save();
    }

    @Override
    public Quest getQuest(Entity npc) {
        return PixelBuiltQuests.getConfig().getQuestFor(
                this.dbConfig.get().getQuestFromNPC(npc)
        );
    }

    @Override
    public void addNPC(Entity npc, Quest quest) {
        this.dbConfig.get().addNPC(npc, quest.getLine().getName(), quest.getId());
        this.dbConfig.save();
    }

    @Override
    public void removeNPC(Entity npc) {
        this.dbConfig.get().removeNPC(npc);
        this.dbConfig.save();
    }

    @Override
    public boolean hasRan(UUID player, Quest quest) {
        return this.dbConfig.get().hasRan(player, quest.getLine().getName(), quest.getId());
    }

    @Override
    public void run(UUID player, Quest quest) {
        this.dbConfig.get().run(player, quest.getLine().getName(), quest.getId());
        this.dbConfig.save();
    }

    @Override
    public Trigger getTriggerAt(Location<World> location) {
        return this.triggersConfig.get().at(location);
    }

    @Override
    public void addTrigger(Trigger trigger) {
        this.triggersConfig.get().triggers.add(trigger);
        this.triggersConfig.save();
    }

    @Override
    public void removeTrigger(Trigger trigger) {
        this.triggersConfig.get().triggers.remove(trigger);
        this.triggersConfig.save();
    }

    @Override
    public List<Trigger> getTriggers() {
        return this.triggersConfig.get().triggers;
    }

    @Override
    public List<String> getQuestsRan(UUID player) {
        return this.dbConfig.get().entries
                .getOrDefault(player, new DatabaseCategory.PlayerEntry())
                .questsRan;
    }

    @Override
    public void resetQuest(UUID player, Quest quest) {
        this.getQuestsRan(player).remove(quest.getLine().getName() + "," + quest.getId());
        this.dbConfig.save();
    }

}
