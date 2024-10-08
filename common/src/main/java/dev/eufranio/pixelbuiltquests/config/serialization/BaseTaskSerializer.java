package dev.eufranio.pixelbuiltquests.config.serialization;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.task.BaseTask;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class BaseTaskSerializer implements TypeSerializer<BaseTask> {

    @Override
    public BaseTask deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String typeId = node.node("type").getString();
        TaskType taskType = PixelBuiltQuests.registry().get(TaskType.class, typeId);
        return node.get(taskType.getValueClass());
    }

    @Override
    public void serialize(Type type, @Nullable BaseTask obj, ConfigurationNode node) throws SerializationException {
        node.set(obj.getType().getValueClass(), obj);
        node.node("type").set(obj.getType().getId());
    }

}
