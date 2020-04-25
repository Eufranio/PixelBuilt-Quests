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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class ProgressRequiredTask implements BaseTask {

    @Setting
    public int id = 4;

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
    public Text getDisplay() {
        String progress = "";
        switch (progressCheckMode) {
            case 1:
                progress = "Min";
                break;
            case 2:
                progress = "Exact";
                break;
            case 3:
                progress = "Max";
                break;
        }
        return Text.of(TextColors.YELLOW, "Progress (", Text.of(TextColors.AQUA, progress, " ", this.progressRequired), ")");
    }

    @Override
    public boolean isCompleted(PlayerData data, QuestLine line, Quest quest) {
        int playerProgress = data.getProgress(line);
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
