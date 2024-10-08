package dev.eufranio.pixelbuiltquests.trigger.listener;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.listeners.TriggerListenerProvider;
import dev.eufranio.pixelbuiltquests.trigger.TriggerQuestReference;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerTypes;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class ClickEntityTriggerListenerProvider extends TriggerListenerProvider {

    final InteractionEvent.InteractEntity clickEntity = (player, entity, hand) -> {
        return click((ServerPlayer) player, entity, hand);
    };

    private EventResult click(ServerPlayer player, Entity entity, InteractionHand hand) {
        if (PixelBuiltQuests.isRunningQuest(player.getUUID())) {
            return EventResult.pass();
        }

        if (!Util.hasPermission(player, "pbq.run")) {
            return EventResult.pass();
        }

        if (triggerType() != TriggerTypes.CLICK_ENTITY) {
            if (triggerType() == TriggerTypes.MAINHAND_CLICK_ENTITY && hand != InteractionHand.MAIN_HAND) {
                return EventResult.pass();
            } else if (triggerType() == TriggerTypes.OFFHAND_CLICK_ENTITY && hand != InteractionHand.OFF_HAND) {
                return EventResult.pass();
            }
        }

        List<TriggerQuestReference> triggers = listeningQuests().stream()
                .filter(q -> q.getTrigger().getEntity().equals(entity.getUUID()))
                .toList();

        if (!triggers.isEmpty()) {
            triggers.forEach(q -> q.getQuest().getExecutor().execute(player, q));
            if (triggers.stream().anyMatch(t -> t.getTrigger().shouldCancelOriginalAction())) {
                return EventResult.interruptFalse();
            } else {
                return EventResult.interruptDefault();
            }
        }

        return EventResult.pass();
    }

    @Override
    public TriggerType triggerType() {
        return TriggerTypes.CLICK_ENTITY;
    }

    @Override
    public void registerListeners() {
        InteractionEvent.INTERACT_ENTITY.register(clickEntity);
    }

    @Override
    public void unregisterListeners() {
        InteractionEvent.INTERACT_ENTITY.unregister(clickEntity);
    }

}
