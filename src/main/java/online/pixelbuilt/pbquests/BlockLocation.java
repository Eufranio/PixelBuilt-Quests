package online.pixelbuilt.pbquests;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * Created by Frani on 06/09/2017.
 */

@ConfigSerializable
public class BlockLocation {

    public static final TypeToken<BlockLocation> type = TypeToken.of(BlockLocation.class);

    @Setting
    public int x;

    @Setting
    public int y;

    @Setting
    public int z;

    @Setting
    public UUID world;

    @Setting
    public String questLine;

    @Setting
    public int questId;

    public BlockLocation(Location<World> location, String questLine, int questId) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getExtent().getUniqueId();
        this.questId = questId;
        this.questLine = questLine;
    }

    public BlockLocation() {}

}
