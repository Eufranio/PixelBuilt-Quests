package online.pixelbuilt.pbquests.config;

import javafx.util.Pair;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
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
    public UUID worldUUID;

    @Setting
    public String questLine;

    @Setting
    public int questId;

    @Setting
    public boolean onWalk = true;

    private Location<World> location;

    public Location<World> getLocation() {
        if (this.location == null) {
            this.location = new Location<World>(Sponge.getServer().getWorld(worldUUID).get(), x, y, z);
        }
        return this.location;
    }

    public Trigger(int x, int y, int z, UUID world, String line, int id, boolean onWalk) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldUUID = world;
        this.questId = id;
        this.questLine = line;
        this.onWalk = onWalk;
    }

    public Trigger(Location<World> loc, String line, int id, boolean onWalk) {
        this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getExtent().getUniqueId(), line, id, onWalk);
    }

    public Trigger(Location<World> loc, Quest quest, Type type) {
        this(loc, quest.getLine().getName(), quest.getId(), type == Type.WALK);
    }

    public Quest getQuest() {
        return PixelBuiltQuests.getConfig().getQuestFor(new Pair<>(questLine, questId));
    }

    public enum Type {

        WALK,
        CLICK;

    }

}
