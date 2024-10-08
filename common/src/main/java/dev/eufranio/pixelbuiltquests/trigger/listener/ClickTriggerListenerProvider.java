package dev.eufranio.pixelbuiltquests.trigger.listener;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.listeners.TriggerListenerProvider;
import dev.eufranio.pixelbuiltquests.trigger.TriggerQuestReference;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerTypes;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ClickTriggerListenerProvider extends TriggerListenerProvider {

    final InteractionEvent.RightClickBlock rightClickBlock = (player, hand, pos, face) -> {
        return click((ServerPlayer) player, pos);
    };

    final InteractionEvent.LeftClickBlock leftClickBlock = (player, hand, pos, face) -> {
        return click((ServerPlayer) player, pos);
    };

    private EventResult click(ServerPlayer player, BlockPos clickedPos) {
        if (PixelBuiltQuests.isRunningQuest(player.getUUID())) {
            return EventResult.pass();
        }

        if (!Util.hasPermission(player, "pbq.run")) {
            return EventResult.pass();
        }

        List<TriggerQuestReference> triggers = listeningQuests().stream()
                .filter(q -> q.getTrigger().getPos().equals(clickedPos))
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
        return TriggerTypes.CLICK;
    }

    @Override
    public void registerListeners() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register(rightClickBlock);
        InteractionEvent.LEFT_CLICK_BLOCK.register(leftClickBlock);
    }

    @Override
    public void unregisterListeners() {
        InteractionEvent.RIGHT_CLICK_BLOCK.unregister(rightClickBlock);
        InteractionEvent.LEFT_CLICK_BLOCK.unregister(leftClickBlock);
    }

}
