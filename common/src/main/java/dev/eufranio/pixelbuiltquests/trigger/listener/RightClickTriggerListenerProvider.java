package dev.eufranio.pixelbuiltquests.trigger.listener;

import dev.architectury.event.events.common.InteractionEvent;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerTypes;

public class RightClickTriggerListenerProvider extends ClickTriggerListenerProvider {

    @Override
    public TriggerType triggerType() {
        return TriggerTypes.RIGHT_CLICK;
    }

    @Override
    public void registerListeners() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register(rightClickBlock);
    }

    @Override
    public void unregisterListeners() {
        InteractionEvent.RIGHT_CLICK_BLOCK.unregister(rightClickBlock);
    }

}
