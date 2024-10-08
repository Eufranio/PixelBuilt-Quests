package dev.eufranio.pixelbuiltquests.quest.executor;

import com.google.common.collect.Lists;
import dev.eufranio.pixelbuiltquests.quest.executor.impl.BaseQuestExecutor;

import java.util.List;

public class QuestExecutorTypes {

    public static final QuestExecutorType DEFAULT = new QuestExecutorType(
            "default",
            "Default",
            BaseQuestExecutor.class,
            new BaseQuestExecutor());

    public static List<QuestExecutorType> defaults() {
        return Lists.newArrayList(DEFAULT);
    }

}
