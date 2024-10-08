package dev.eufranio.pixelbuiltquests.listeners;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.task.QuestTaskReference;
import dev.eufranio.pixelbuiltquests.task.TaskType;

import java.util.List;
import java.util.stream.Collectors;

public abstract class TaskListenerProvider implements ListenerProvider {

    abstract public TaskType taskType();

    public List<QuestTaskReference> listeningTasks() {
        return PixelBuiltQuests.registry().allOfType(QuestLine.class)
                .stream()
                .flatMap(line -> line.getQuests().stream().map(q -> QuestReference.of(line.getId(), q)))
                .flatMap(quest -> quest.getQuest().getTasks().stream()
                        .filter(t -> t.getType() == this.taskType())
                        .map(task -> QuestTaskReference.of(quest, task)))
                .collect(Collectors.toList());
    }

}
