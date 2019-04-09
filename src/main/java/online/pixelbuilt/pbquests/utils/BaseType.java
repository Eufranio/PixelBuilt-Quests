package online.pixelbuilt.pbquests.utils;

import org.spongepowered.api.CatalogType;

/**
 * Created by Frani on 07/04/2019.
 */
public interface BaseType<T> extends CatalogType {

    Class<? extends T> getValueClass();

}
