package dev.eufranio.pixelbuiltquests.trigger;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.storage.sql.Trigger;

public class TriggerQuestReference extends QuestReference {

    final int idTrigger;

    public static TriggerQuestReference of(Trigger trigger) {
        return new TriggerQuestReference(
                trigger.getQuest().getQuestLine().getId(),
                trigger.getQuest().getQuest().getId(),
                trigger.getId());
    }

    protected TriggerQuestReference(String idQuestLine, String idQuest, int idTrigger) {
        super(idQuestLine, idQuest);
        this.idTrigger = idTrigger;
    }

    public Trigger getTrigger() {
        return PixelBuiltQuests.triggerManager()
                .getTriggers()
                .stream()
                .filter(t -> t.getId() == this.idTrigger)
                .findFirst()
                .orElse(null);
    }

}
