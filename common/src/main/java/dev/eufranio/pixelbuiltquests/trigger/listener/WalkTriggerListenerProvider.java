package dev.eufranio.pixelbuiltquests.trigger.listener;

import dev.architectury.event.EventResult;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.events.internal.PBQInternalEvents;
import dev.eufranio.pixelbuiltquests.listeners.TriggerListenerProvider;
import dev.eufranio.pixelbuiltquests.trigger.TriggerQuestReference;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerTypes;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class WalkTriggerListenerProvider extends TriggerListenerProvider {

    final PBQInternalEvents.MoveEntity callback = (entity, world, from, to) -> {
        if (!(entity instanceof ServerPlayer)) {
            return EventResult.pass();
        }

        final ServerPlayer player = (ServerPlayer) entity;
        if (PixelBuiltQuests.isRunningQuest(player.getUUID())) {
            return EventResult.pass();
        }

        final BlockPos toPos = Util.blockPos(to);
        if (Util.blockPos(from).equals(toPos)) {
            return EventResult.pass();
        }

        if (!Util.hasPermission(player, "pbq.run")) {
            return EventResult.pass();
        }

        final BlockPos triggerPos = toPos.below();
        List<TriggerQuestReference> triggers = listeningQuests().stream()
                .filter(q -> q.getTrigger().getPos().equals(triggerPos))
                .toList();
        var toReturn = EventResult.pass();
        if (triggers.stream().anyMatch(t -> t.getTrigger().shouldCancelOriginalAction())) {
            toReturn = EventResult.interruptDefault();
        }

        triggers.forEach(q -> q.getQuest().getExecutor().execute(player, q));
        return toReturn;
    };

    @Override
    public TriggerType triggerType() {
        return TriggerTypes.WALK;
    }

    @Override
    public void registerListeners() {
        PBQInternalEvents.MOVE_ENTITY.register(callback);
    }

    @Override
    public void unregisterListeners() {
        PBQInternalEvents.MOVE_ENTITY.unregister(callback);
    }

}
