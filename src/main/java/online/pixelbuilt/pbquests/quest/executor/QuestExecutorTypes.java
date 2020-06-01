package online.pixelbuilt.pbquests.quest.executor;

import com.google.common.collect.Lists;
import online.pixelbuilt.pbquests.quest.executor.impl.BaseQuestExecutor;

import java.util.List;

/**
 * Created by Frani on 23/01/2019.
 */
public class QuestExecutorTypes {

    public static final QuestExecutorType DEFAULT = new QuestExecutorType("default", "Default", BaseQuestExecutor.class);

    public static List<QuestExecutorType> defaults() {
        return Lists.newArrayList(DEFAULT);
    }

}
