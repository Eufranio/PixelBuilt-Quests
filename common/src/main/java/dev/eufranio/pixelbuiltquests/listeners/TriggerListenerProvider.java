package dev.eufranio.pixelbuiltquests.listeners;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.trigger.TriggerQuestReference;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;

import java.util.List;
import java.util.stream.Collectors;

public abstract class TriggerListenerProvider implements ListenerProvider {

    abstract public TriggerType triggerType();

    public List<TriggerQuestReference> listeningQuests() {
        return PixelBuiltQuests.triggerManager().getTriggers()
                .stream()
                .filter(t -> t.getType() == this.triggerType())
                .map(TriggerQuestReference::of)
                .collect(Collectors.toList());
    }

}
