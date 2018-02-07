package online.pixelbuilt.pbquests.config;

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
    public String name = "default";

    @Setting
    public List<Quest> quests = Lists.newArrayList(new Quest());

}
