package online.pixelbuilt.pbquests.reward;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

/**
 * Created by Frani on 20/01/2019.
 */
@CatalogedBy(RewardTypes.class)
public class RewardType implements CatalogType {

    private String id;
    private String name;
    private Class<? extends BaseReward> reward;

    public RewardType(String id, String name, Class<? extends BaseReward> reward) {
        this.id = id;
        this.name = name;
        this.reward = reward;
    }

    @Override
    public String getId() {
        return "pbq:" + this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Class<? extends BaseReward> getCatalogClass() {
        return this.reward;
    }
}