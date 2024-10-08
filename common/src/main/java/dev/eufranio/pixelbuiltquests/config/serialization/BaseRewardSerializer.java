package dev.eufranio.pixelbuiltquests.config.serialization;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.reward.BaseReward;
import dev.eufranio.pixelbuiltquests.reward.RewardType;
import dev.eufranio.pixelbuiltquests.task.BaseTask;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class BaseRewardSerializer implements TypeSerializer<BaseReward> {

    @Override
    public BaseReward deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String typeId = node.node("type").getString();
        RewardType taskType = PixelBuiltQuests.registry().get(RewardType.class, typeId);
        return node.get(taskType.getValueClass());
    }

    @Override
    public void serialize(Type type, @Nullable BaseReward obj, ConfigurationNode node) throws SerializationException {
        node.set(obj.getType().getValueClass(), obj);
        node.node("type").set(obj.getType().getId());
    }

}
