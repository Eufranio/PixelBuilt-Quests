package dev.eufranio.pixelbuiltquests.quest.executor;

import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import net.minecraft.server.level.ServerPlayer;

public interface QuestExecutor {

    default void execute(ServerPlayer player, QuestReference reference) {
        this.execute(player, reference, true);
    }

    void execute(ServerPlayer player, QuestReference reference, boolean sendIncompleteTasksMessage);

}
