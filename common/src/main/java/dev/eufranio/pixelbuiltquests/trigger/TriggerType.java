package dev.eufranio.pixelbuiltquests.trigger;

import dev.eufranio.pixelbuiltquests.listeners.ListenerProvider;
import dev.eufranio.pixelbuiltquests.registry.BaseType;
import dev.eufranio.pixelbuiltquests.registry.IdSerializable;
import dev.eufranio.pixelbuiltquests.trigger.creator.TriggerCreator;
import org.jetbrains.annotations.NotNull;

public class TriggerType implements BaseType, IdSerializable {

    final String id;
    final String name;
    final ListenerProvider listenerProvider;
    final TriggerCreator triggerCreatorCallback;

    public TriggerType(String id,
                       String name,
                       @NotNull ListenerProvider listenerProvider,
                       @NotNull TriggerCreator triggerCreatorCallback) {
        this.id = id;
        this.name = name;
        this.listenerProvider = listenerProvider;
        this.triggerCreatorCallback = triggerCreatorCallback;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public ListenerProvider getListenerProvider() {
        return listenerProvider;
    }

    public TriggerCreator getTriggerCreator() {
        return triggerCreatorCallback;
    }
}
