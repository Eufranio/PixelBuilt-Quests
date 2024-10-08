package dev.eufranio.pixelbuiltquests.trigger.listener;

import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerTypes;

public class OffClickEntityTriggerListenerProvider extends ClickEntityTriggerListenerProvider {

    @Override
    public TriggerType triggerType() {
        return TriggerTypes.OFFHAND_CLICK_ENTITY;
    }

}
