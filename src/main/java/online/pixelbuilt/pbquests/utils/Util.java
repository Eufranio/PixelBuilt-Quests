package online.pixelbuilt.pbquests.utils;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * Created by Frani on 04/04/2018.
 */
public class Util {

    public static Text toText(String s) {
        return TextSerializers.FORMATTING_CODE.deserialize(s);
    }

}
