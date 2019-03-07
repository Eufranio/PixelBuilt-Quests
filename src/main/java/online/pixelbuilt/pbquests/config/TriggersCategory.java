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
                trigger.getPosition().equals(loc.getBlockPosition()) &&
                        trigger.worldUUID.equals(loc.getExtent().getUniqueId())
        ).findFirst().orElse(null);
    }

}
