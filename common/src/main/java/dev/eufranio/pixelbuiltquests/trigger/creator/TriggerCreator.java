package dev.eufranio.pixelbuiltquests.trigger.creator;

import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.storage.sql.Trigger;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface TriggerCreator {

    CompletableFuture<Trigger> createNewTrigger(ServerPlayer player, TriggerType type, QuestReference quest, boolean cancelOriginalAction);

}