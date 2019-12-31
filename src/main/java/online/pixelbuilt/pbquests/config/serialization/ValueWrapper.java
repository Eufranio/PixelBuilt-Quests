package online.pixelbuilt.pbquests.config.serialization;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import online.pixelbuilt.pbquests.reward.RewardType;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.utils.BaseType;
import org.spongepowered.api.CatalogType;

/**
 * Created by Frani on 27/02/2019.
 */
@SuppressWarnings("unchecked")
public class ValueWrapper<T> {
    private T value;
    private BaseType type;
    public ValueWrapper(T obj, BaseType type) {
        this.value = obj;
        this.type = type;
    }

    public T getValue() {
        return this.value;
    }

    public BaseType getType() {
        return this.type;
    }

    /**
     * Created by Frani on 27/02/2019.
     */
    public static class ValueWrapperTypeSerializer implements TypeSerializer<ValueWrapper> {

        @Override
        public ValueWrapper deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
            try {
                BaseType type = null;
                try {
                    type = configurationNode.getNode("type").getValue(TypeToken.of(TaskType.class));
                } catch (ObjectMappingException e1) {
                    type = configurationNode.getNode("type").getValue(TypeToken.of(RewardType.class));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                Class clazz = type.getValueClass();
                //Class clazz = Class.forName(configurationNode.getNode("class").getString());
                return new ValueWrapper(configurationNode.getNode("value").getValue(TypeToken.of(clazz), clazz.newInstance()), type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void serialize(TypeToken<?> typeToken, ValueWrapper valueWrapper, ConfigurationNode configurationNode) throws ObjectMappingException {
            try {
                Object type = valueWrapper.getType();
                TypeToken token1 = TypeToken.of(type.getClass());
                configurationNode.getNode("type").setValue(token1, type);

                Object value = valueWrapper.getValue();
                TypeToken token = TypeToken.of(value.getClass());
                configurationNode.getNode("value").setValue(token, value);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

}