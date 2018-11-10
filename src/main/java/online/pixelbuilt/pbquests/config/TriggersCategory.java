package online.pixelbuilt.pbquests.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Frani on 17/12/2017.
 */
@ConfigSerializable
public class TriggersCategory {

    @Setting
    public List<Trigger> triggers = new ArrayList<>();

    public Trigger at(Location<World> loc) {
        return triggers.stream().filter(trigger ->
                trigger.getLocation().getBlockX() == loc.getBlockX() &&
                trigger.getLocation().getBlockY() == loc.getBlockY() &&
                trigger.getLocation().getBlockZ() == loc.getBlockZ() &&
                trigger.getLocation().getExtent().getUniqueId().equals(loc.getExtent().getUniqueId())
        ).findFirst().orElse(null);
    }

    public void add(Location<World> loc, String questLine, int questId, boolean onWalk) {
        triggers.add(new Trigger(loc, questLine, questId, onWalk));
        PixelBuiltQuests.instance.onReload(null);
    }

    public void remove(Location<World> loc) {
        triggers.removeIf(t -> t.getLocation().equals(loc));
        PixelBuiltQuests.instance.onReload(null);
    }

}
