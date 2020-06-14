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

    public static String timeDiffFormat(long timeDiffSeconds, boolean includeSeconds) {
        String timeFormat;
        int seconds = (int) timeDiffSeconds % 60;
        timeDiffSeconds = timeDiffSeconds / 60;
        int minutes = (int) timeDiffSeconds % 60;
        timeDiffSeconds = timeDiffSeconds / 60;
        int hours = (int) timeDiffSeconds % 24;
        timeDiffSeconds = timeDiffSeconds / 24;
        int days = (int) timeDiffSeconds;

        if (days > 7) {
            timeFormat = days + " days";
        } else if (days > 0) {
            timeFormat = days + "d " + hours + "h";
        } else if (days == 0 && hours > 0) {
            if (includeSeconds) {
                timeFormat = hours + "h " + minutes + "m " + seconds + "s";
            } else {
                timeFormat = hours + "h " + minutes + "m";
            }
        } else if (days == 0 && hours == 0 && minutes > 0) {
            if (includeSeconds) {
                timeFormat = minutes + "m " + seconds + "s";
            } else {
                timeFormat = minutes + "m";
            }
        } else {
            timeFormat = seconds + "s";
        }

        return timeFormat;
    }

}
