package online.pixelbuilt.pbquests.quest;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class QuestLine {

    @Setting
    private String name = "default";

    @Setting
    public List<Integer> quests = Lists.newArrayList(0);

    public String getName() {
        return this.name;
    }

}
