package online.pixelbuilt.pbquests.task.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.BaseTask;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Frani on 28/01/2019.
 */
@ConfigSerializable
public class VisitTask implements BaseTask {

    @Setting
    public String defaultVisitLocation = "0,0,0,world";

    @Setting
    public int defaultVisitRadius = 5;

    public static Multimap<VisitTask, UUID> locations = ArrayListMultimap.create();

    @Override
    public boolean complete(Map<String, String> options, Player player, Quest quest, QuestLine line, int questId) {
        Location<World> loc = this.getLocation(options);
        if (loc == null) return true;

        int visitRadius = Integer.parseInt(options.getOrDefault("visitRadius", ""+this.defaultVisitRadius));
        if (!locations.containsKey(this)) {
            locations.put(this, UUID.randomUUID());
        }

        for (Map.Entry<VisitTask, UUID> e : locations.entries()) {
            if (e.getValue().equals(player.getUniqueId()) &&
                    e.getKey().getLocation(options).getExtent().equals(loc.getExtent()) &&
                    e.getKey().getLocation(options).getBlockPosition().distanceSquared(loc.getBlockPosition()) <= visitRadius) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitTask)) return false;
        return true;
    }

    public Location<World> getLocation(Map<String, String> options) {
        String[] string = options.getOrDefault("visitLocation", this.defaultVisitLocation).split(",");
        World world = Sponge.getServer().getWorld(string[3]).orElse(null);
        if (world == null) return null;
        return new Location<>(world, Integer.parseInt(string[0]), Integer.parseInt(string[1]), Integer.parseInt(string[2]));
    }

}
