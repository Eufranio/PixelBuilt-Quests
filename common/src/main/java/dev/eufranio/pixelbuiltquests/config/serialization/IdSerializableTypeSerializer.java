package dev.eufranio.pixelbuiltquests.config.serialization;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.registry.IdSerializable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class IdSerializableTypeSerializer implements TypeSerializer<IdSerializable> {

        @Override
        public IdSerializable deserialize(Type type, ConfigurationNode node) throws SerializationException {
            Class<? extends IdSerializable> typeClass = (Class<? extends IdSerializable>) type;
            return PixelBuiltQuests.registry().get(typeClass, node.getString());
        }

        @Override
        public void serialize(Type type, @Nullable IdSerializable obj, ConfigurationNode node) throws SerializationException {
            node.set(obj.getId());
        }

}
