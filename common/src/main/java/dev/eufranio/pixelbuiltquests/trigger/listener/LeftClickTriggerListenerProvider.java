package dev.eufranio.pixelbuiltquests.trigger.listener;

import dev.architectury.event.events.common.InteractionEvent;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerTypes;

public class LeftClickTriggerListenerProvider extends ClickTriggerListenerProvider {

    @Override
    public TriggerType triggerType() {
        return TriggerTypes.LEFT_CLICK;
    }

    @Override
    public void registerListeners() {
        InteractionEvent.LEFT_CLICK_BLOCK.register(leftClickBlock);
    }

    @Override
    public void unregisterListeners() {
        InteractionEvent.LEFT_CLICK_BLOCK.unregister(leftClickBlock);
    }

}
