package dev.eufranio.pixelbuiltquests.quest;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;

public class QuestReference {

    final String idQuestLine;
    final String idQuest;

    public static QuestReference of(QuestLine line, Quest quest) {
        return new QuestReference(line.getId(), quest.getId());
    }

    public static QuestReference of(String line, String quest) {
        return new QuestReference(line, quest);
    }

    protected QuestReference(String idQuestLine, String idQuest) {
        this.idQuestLine = idQuestLine;
        this.idQuest = idQuest;
    }

    public Quest getQuest() {
        return PixelBuiltQuests.registry().get(Quest.class, this.idQuest);
    }

    public QuestLine getQuestLine() {
        return PixelBuiltQuests.registry().get(QuestLine.class, this.idQuestLine);
    }

}
