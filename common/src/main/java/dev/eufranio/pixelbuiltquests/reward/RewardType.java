package dev.eufranio.pixelbuiltquests.reward;

import dev.eufranio.pixelbuiltquests.utils.WrappedValueType;

import java.util.function.Supplier;

public class RewardType implements WrappedValueType<BaseReward> {

    final String id;
    final String name;
    final Class<? extends BaseReward> reward;
    final Supplier<BaseReward> factory;

    public RewardType(String id,
                      String name,
                      Class<? extends BaseReward> reward,
                      Supplier<BaseReward> factory) {
        if (!id.contains(":")) {
            id = "pbq:" + id;
        }
        this.id = id;
        this.name = name;
        this.reward = reward;
        this.factory = factory;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<? extends BaseReward> getValueClass() {
        return this.reward;
    }

    public BaseReward newInstance() {
        return this.factory.get();
    }

}