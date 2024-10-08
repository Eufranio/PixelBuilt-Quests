package dev.eufranio.pixelbuiltquests.trigger.creator;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.storage.sql.Trigger;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;

public class SelectEntityTriggerCreator implements TriggerCreator {

    @Override
    public CompletableFuture<Trigger> createNewTrigger(ServerPlayer player, TriggerType type, QuestReference quest, boolean cancelOriginalAction) {
        player.sendSystemMessage(Component.empty().withStyle(ChatFormatting.AQUA)
                .append("Right click the entity you want to select for the trigger"));
        CompletableFuture<Trigger> future = new CompletableFuture<>();
        InteractionEvent.INTERACT_ENTITY.register(new InteractionEvent.InteractEntity() {
            @Override
            public EventResult interact(Player player, Entity entity, InteractionHand hand) {
                if (player.getUUID().equals(player.getUUID())) {
                    InteractionEvent.INTERACT_ENTITY.unregister(this);
                    player.sendSystemMessage(Component.empty().withStyle(ChatFormatting.AQUA)
                            .append("Successfully selected entity"));
                    Trigger trigger = new Trigger(quest,
                            entity.getUUID(),
                            entity.blockPosition(),
                            entity.level().dimension(),
                            type,
                            cancelOriginalAction);
                    future.complete(trigger);
                    return EventResult.interruptDefault();
                }
                return EventResult.pass();
            }
        });
        return future;
    }

}
