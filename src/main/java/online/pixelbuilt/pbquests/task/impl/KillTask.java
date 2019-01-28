package online.pixelbuilt.pbquests.task.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.BaseTask;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Frani on 28/01/2019.
 */
@ConfigSerializable
public class KillTask implements BaseTask {

    public static Multimap<UUID, EntityType> killed = ArrayListMultimap.create();

    @Setting(comment = "checking mode. 1 = killed at least one specific mob, 2 = killed at least X specific mobs, " +
            "3 = killed at least one mob, 4 = killed at least X mobs")
    public int defaultCheckMode = 1;

    @Setting
    public int defaultMobCount = 5;

    @Setting
    public EntityType defaultMob = EntityTypes.ZOMBIE;

    @Override
    public boolean complete(Map<String, String> options, Player player, Quest quest, QuestLine line, int questId) {
        int checkMode = Integer.parseInt(options.getOrDefault("checkMode", ""+this.defaultCheckMode));
        int mobCount = Integer.parseInt(options.getOrDefault("mobCount", ""+this.defaultMobCount));
        EntityType mob = Sponge.getRegistry().getType(EntityType.class, options.getOrDefault("mob", this.defaultMob.getId())).orElse(this.defaultMob);

        if (checkMode == 1) {
            return killed.get(player.getUniqueId()).contains(mob);
        } else if (checkMode == 2) {
            return killed.get(player.getUniqueId()).stream().filter(mob::equals).count() >= mobCount;
        } else if (checkMode == 3) {
            return !killed.get(player.getUniqueId()).isEmpty();
        } else if (checkMode == 4) {
            return killed.get(player.getUniqueId()).size() >= mobCount;
        }

        return false;
    }
}
