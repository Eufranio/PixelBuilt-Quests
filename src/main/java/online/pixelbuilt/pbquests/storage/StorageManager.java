package online.pixelbuilt.pbquests.storage;

import com.google.common.collect.Maps;
import io.github.eufranio.storage.Persistable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.storage.sql.Trigger;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Frani on 10/11/2018.
 */
public class StorageManager {

    private DataSource src;
    private PixelBuiltQuests plugin;

    private Persistable<PlayerData, UUID> playerData;
    private Persistable<QuestStatus, Integer> status;
    private Persistable<Trigger, UUID> triggerData;

    private Map<UUID, Trigger> triggers = Maps.newHashMap();

    public StorageManager(PixelBuiltQuests plugin) {
        this.plugin = plugin;
    }

    public void init() {
        try {
            this.status = Persistable.create(QuestStatus.class, ConfigManager.getConfig().database.url);
            this.playerData = Persistable.create(PlayerData.class, ConfigManager.getConfig().database.url);
            this.triggerData = Persistable.create(Trigger.class, ConfigManager.getConfig().database.url);

            this.triggerData.objDao.queryForAll().forEach(t -> this.triggers.put(t.npc, t));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData getData(UUID uuid) {
        PlayerData data = this.playerData.getOrCreate(uuid);
        try {
            data.refresh();
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    public void save(PlayerData data) {
        this.playerData.save(data);
    }

    public Tuple<Quest, QuestLine> getQuest(Entity npc) {
        Trigger trigger = this.triggerData.get(npc.getUniqueId());
        if (trigger != null)
            return trigger.getQuest();
        return null;
    }

    public void addNPC(Entity npc, QuestLine line, Quest quest) {
        addTrigger(new Trigger(npc.getLocation(), quest, line, Trigger.Type.NPC, npc.getUniqueId()));
    }

    public void removeNPC(Entity npc) {
        this.triggerData.delete(this.triggers.remove(npc.getUniqueId()));
    }

    public Trigger getTriggerAt(Location<World> location) {
        return this.triggers.values().stream()
                .filter(t -> t.location.getExtent().getUniqueId().equals(location.getExtent().getUniqueId()))
                .filter(t -> t.location.getBlockPosition().equals(location.getBlockPosition()))
                .findFirst()
                .orElse(null);
    }

    public void addTrigger(Trigger trigger) {
        this.triggerData.save(trigger);
        this.triggers.put(trigger.npc, trigger);
    }

    public void removeTrigger(Trigger trigger) {
        this.triggerData.delete(this.triggers.remove(trigger.npc));
    }

}
