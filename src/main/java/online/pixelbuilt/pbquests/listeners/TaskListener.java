package online.pixelbuilt.pbquests.listeners;

import com.google.common.collect.ArrayListMultimap;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.task.TriggeredTask;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;

public class TaskListener {

    private PixelBuiltQuests plugin;
    public TaskListener(PixelBuiltQuests plugin) {
        this.plugin = plugin;
    }

    ArrayListMultimap<Class<?>, ListenerHolder> eventsToListen = ArrayListMultimap.create();

    @Listener
    public void onEvent(Event event) {
        eventsToListen.asMap().forEach((clazz, list) -> {
            if (event.getClass().isAssignableFrom(clazz)) {
                list.forEach(holder -> holder.task.handle(holder.line, holder.quest, event));
            }
        });
    }

    public void reloadEvents() {
        this.eventsToListen.clear();
        ConfigManager.getLines().forEach(line ->
                line.quests.forEach(i -> {
                    Quest quest = ConfigManager.getQuest(i);
                    quest.tasks.forEach(t -> {
                        BaseTask task = t.getValue();
                        if (task instanceof TriggeredTask) {
                            TriggeredTask triggeredTask = (TriggeredTask) task;
                            this.eventsToListen.put(triggeredTask.getEventClass(), new ListenerHolder(line, quest, triggeredTask));
                        }
                    });
                })
        );
    }

    static class ListenerHolder {

        QuestLine line;
        Quest quest;
        TriggeredTask task;

        public ListenerHolder(QuestLine line, Quest quest, TriggeredTask task) {
            this.line = line;
            this.quest = quest;
            this.task = task;
        }
    }

}
