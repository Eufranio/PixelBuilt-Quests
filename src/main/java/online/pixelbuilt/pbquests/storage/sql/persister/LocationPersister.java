package online.pixelbuilt.pbquests.storage.sql.persister;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class LocationPersister extends StringType {

    private static final LocationPersister INSTANCE = new LocationPersister();

    private LocationPersister() {
        super(SqlType.STRING, new Class<?>[] { Location.class });
    }

    public static LocationPersister getSingleton() {
        return INSTANCE;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        Location<World> obj = (Location<World>) javaObject;
        if (obj == null)
            return null;
        return obj.getBlockX() + "," + obj.getBlockY() + "," + obj.getBlockZ() + "," + obj.getExtent().getUniqueId().toString();
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        if (sqlArg == null)
            return null;
        String[] array = sqlArg.toString().split(",");
        World world = Sponge.getServer().getWorld(UUID.fromString(array[3])).orElse(null);
        if (world == null) {
            PixelBuiltQuests.getInstance().logger.error("There's an invalid world saved in the PixelBuilt-Quests database, leading to an invalid trigger. If you have reset your " +
                    "world, make sure to delete the PBQ database as well! This could also be caused by having triggers on unloaded worlds.");
            return null;
        }
        return new Location<>(world, Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2]));
    }

}