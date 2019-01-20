package online.pixelbuilt.pbquests.config;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.quest.QuestLine;

import java.util.List;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class QuestLinesCategory {

    @Setting
    public List<QuestLine> questLines = Lists.newArrayList(new QuestLine());

}
