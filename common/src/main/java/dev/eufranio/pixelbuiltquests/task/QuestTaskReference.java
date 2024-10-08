package dev.eufranio.pixelbuiltquests.task;

import dev.eufranio.pixelbuiltquests.quest.Quest;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;

public class QuestTaskReference extends QuestReference {

    final String idTask;

    public static QuestTaskReference of(QuestReference quest, BaseTask task) {
        return of(quest.getQuestLine().getId(), quest.getQuest().getId(), task.getId());
    }

    public static QuestTaskReference of(QuestLine line, Quest quest, BaseTask task) {
        return of(line.getId(), quest.getId(), task.getId());
    }

    public static QuestTaskReference of(String line, String quest, String task) {
        return new QuestTaskReference(line, quest, task);
    }

    protected QuestTaskReference(String idQuestLine, String idQuest, String idTask) {
        super(idQuestLine, idQuest);
        this.idTask = idTask;
    }

    public BaseTask getTask() {
        return getQuest().getTasks().stream()
                .filter(t -> t.getId().equals(this.idTask))
                .findFirst()
                .get();
    }

}
