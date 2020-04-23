package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import online.pixelbuilt.pbquests.utils.Util;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class ProgressRequiredTask implements BaseTask {

    @Setting
    public int id;

    @Setting(comment = "1 = min, 2 = exact, 3 = max")
    public int progressCheckMode = 3;

    @Setting
    public int progressRequired = 0;

    @Setting
    public boolean sendMessage = true;

    @Override
    public TaskType getType() {
        return TaskTypes.PROGRESS_REQUIRED;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isCompleted(PlayerData data, QuestLine line, Quest quest) {
        int playerProgress = PixelBuiltQuests.getStorage().getProgress(data.id, line);
        switch (progressCheckMode) {
            case 1:
                if (playerProgress < progressRequired) {
                    if (sendMessage)
                        data.getUser().getPlayer().ifPresent(p ->
                                p.sendMessage(Util.toText(ConfigManager.getConfig().messages.noProgressMin
                                        .replace("%progress%", ""+progressRequired)
                                ))
                        );
                    return false;
                }
                break;
            case 2:
                if (playerProgress != progressRequired) {
                    if (sendMessage)
                        data.getUser().getPlayer().ifPresent(p ->
                                p.sendMessage(Util.toText(ConfigManager.getConfig().messages.noProgressExact
                                        .replace("%progress%", ""+progressRequired)
                                ))
                        );
                    return false;
                }
                break;
            case 3:
                if (playerProgress > progressRequired) {
                    if (sendMessage)
                        data.getUser().getPlayer().ifPresent(p ->
                                p.sendMessage(Util.toText(ConfigManager.getConfig().messages.noProgressMax
                                        .replace("%progress%", ""+progressRequired)
                                ))
                        );
                    return false;
                }
                break;
        }
        return true;
    }

}
