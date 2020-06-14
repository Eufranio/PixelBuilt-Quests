package online.pixelbuilt.pbquests.utils;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Frani on 04/04/2018.
 */
public class Util {

    public static String fromText(Text text) {
        return TextSerializers.FORMATTING_CODE.serialize(text);
    }

    public static Text toText(String s) {
        return TextSerializers.FORMATTING_CODE.deserialize(s);
    }

    public static Text locationToText(Location<World> loc) {
        return Text.of(
                TextColors.YELLOW, "world=" + loc.getExtent().getName() + ", x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getZ()
        );
    }

}
