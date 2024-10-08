package dev.eufranio.pixelbuiltquests.utils;

import dev.eufranio.pixelbuiltquests.registry.BaseType;
import dev.eufranio.pixelbuiltquests.registry.IdSerializable;

public interface WrappedValueType<T> extends BaseType, IdSerializable {

    Class<? extends T> getValueClass();

}
