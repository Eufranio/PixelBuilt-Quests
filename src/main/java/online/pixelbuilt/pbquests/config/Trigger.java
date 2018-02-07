package online.pixelbuilt.pbquests.config;

import javafx.util.Pair;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * Created by Frani on 17/12/2017.
 */
@ConfigSerializable
public class Trigger {

    public Trigger(){}

    @Setting
    public int x;

    @Setting
    public int y;

    @Setting
    public int z;

    @Setting
    public String worldUUID;

    @Setting
    public String questLine;

    @Setting
    public int questId;

    private Location<World> location;

    public Location<World> getLocation() {
        if (this.location == null) {
            this.location = new Location<World>(Sponge.getServer().getWorld(UUID.fromString(worldUUID)).get(), x, y, z);
        }
        return this.location;
    }

    public Trigger(Location<World> loc, String questLine, int questId) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.worldUUID = loc.getExtent().getUniqueId().toString();
        this.questId = questId;
        this.questLine = questLine;
        this.location = loc;
    }

    public Quest getQuest() {
        return PixelBuiltQuests.getConfig().getQuestFor(new Pair<>(questLine, questId));
    }

}
