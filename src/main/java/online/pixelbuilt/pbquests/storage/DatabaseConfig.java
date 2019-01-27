package online.pixelbuilt.pbquests.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Tuple;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Frani on 18/12/2017.
 */
@ConfigSerializable
public class DatabaseConfig {

    @Setting
    Map<UUID, PlayerEntry> entries = Maps.newConcurrentMap();

    @ConfigSerializable
    public static class PlayerEntry {

        @Setting
        Map<String, Integer> questLines = Maps.newConcurrentMap();

        @Setting
        List<String> questsRan = Lists.newArrayList(); // "line,id"

    }

    @Setting
    private Map<UUID, NPCEntry> npcEntryMap = Maps.newConcurrentMap();

    @ConfigSerializable
    public static class NPCEntry {

        public NPCEntry(){}

        NPCEntry(int id, String line) {
            this.line = line;
            this.id = id;
        }

        @Setting
        public String line;

        @Setting
        public int id;

    }

    int getProgress(UUID player, String line) {
        PlayerEntry entry = entries.get(player);
        if (entry != null) {
            Integer progress = entry.questLines.get(line);
            if (progress != null) {
                return progress;
            }
        }
        return 0;
    }

    void setProgress(UUID player, String line, int progress) {
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

    Tuple<Quest, QuestLine> getQuestFromNPC(Entity npc) {
        NPCEntry npcEntry = this.npcEntryMap.get(npc.getUniqueId());
        return npcEntry != null ?
                new Tuple<>(ConfigManager.getQuest(npcEntry.id), ConfigManager.getLine(npcEntry.line)) :
                null;
    }

    void removeNPC(Entity npc) {
        this.npcEntryMap.remove(npc.getUniqueId());
    }

    void addNPC(Entity npc, String questLine, int questId) {
        removeNPC(npc);
        this.npcEntryMap.put(npc.getUniqueId(), new NPCEntry(questId, questLine));
    }

    boolean hasRan(UUID player, String quest, int id) {
        PlayerEntry entry = this.entries.get(player);
        if (entry == null) return false;
        return entry.questsRan.contains(quest + "," + id);
    }

    void run(UUID player, String quest, int id) {
        PlayerEntry entry = this.entries.get(player);
        if (entry == null) {
            entry = new PlayerEntry();
            this.entries.put(player, entry);
        }
        entry.questsRan.add(quest + "," + id);
    }

}
