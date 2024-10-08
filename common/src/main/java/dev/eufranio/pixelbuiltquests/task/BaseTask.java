package dev.eufranio.pixelbuiltquests.task;

import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import net.minecraft.network.chat.MutableComponent;

import java.util.UUID;

public interface BaseTask {

    TaskType getType();

    String getId();

    boolean isCompleted(UUID player, QuestReference reference);

    MutableComponent toText();

}
