package dev.eufranio.pixelbuiltquests.trigger.listener;

import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerTypes;

public class MainClickEntityTriggerListenerProvider extends ClickEntityTriggerListenerProvider {

    @Override
    public TriggerType triggerType() {
        return TriggerTypes.MAINHAND_CLICK_ENTITY;
    }

}
