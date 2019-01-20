package online.pixelbuilt.pbquests.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Frani on 18/12/2017.
 */
@ConfigSerializable
public class DatabaseCategory {

    @Setting
    public Map<UUID, PlayerEntry> entries = Maps.newConcurrentMap();

    @ConfigSerializable
    public static class PlayerEntry {

        @Setting
        public Map<String, Integer> questLines = Maps.newConcurrentMap();

        @Setting
        public List<String> questsRan = Lists.newArrayList(); // "line,id"

    }

    @Setting
    public Map<UUID, NPCEntry> npcEntryMap = Maps.newConcurrentMap();

    @ConfigSerializable
    public static class NPCEntry {

        public NPCEntry(){}

        public NPCEntry(int id, String line) {
            this.line = line;
            this.id = id;
        }

        @Setting
        public String line;

        @Setting
        public int id;

    }

    public int getProgress(UUID player, String line) {
        PlayerEntry entry = entries.get(player);
        if (entry != null) {
            Integer progress = entry.questLines.get(line);
            if (progress != null) {
                return progress;
            }
        }
        return 0;
    }

    public void setProgress(UUID player, String line, int progress) {
        PlayerEntry entry = this.entries.get(player);
        if (entry == null) {
            entry = new PlayerEntry();
            this.entries.put(player, entry);
        }
        Integer playerProgress = entry.questLines.get(line);
        if (playerProgress != null) {
            entry.questLines.replace(line, progress);
        } else {
            entry.questLines.put(line, progress);
        }
    }

    public Pair<String, Integer> getQuestFromNPC(Entity npc) {
        NPCEntry npcEntry = this.npcEntryMap.get(npc.getUniqueId());
        return npcEntry != null ? new Pair<>(npcEntry.line, npcEntry.id) : null;
    }

    public void removeNPC(Entity npc) {
        this.npcEntryMap.remove(npc.getUniqueId());
    }

    public void addNPC(Entity npc, String questLine, int questId) {
        removeNPC(npc);
        this.npcEntryMap.put(npc.getUniqueId(), new NPCEntry(questId, questLine));
    }

    public boolean hasRan(UUID player, String quest, int id) {
        PlayerEntry entry = this.entries.get(player);
        if (entry == null) return false;
        return entry.questsRan.contains(quest + "," + id);
    }

    public void run(UUID player, String quest, int id) {
        PlayerEntry entry = this.entries.get(player);
        if (entry == null) {
            entry = new PlayerEntry();
            this.entries.put(player, entry);
        }
        entry.questsRan.add(quest + "," + id);
    }

}
