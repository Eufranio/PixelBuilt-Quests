package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class OneTimeTaskReward implements BaseTask, BaseReward<OneTimeTaskReward> {

    @Setting
    public int id;

    @Setting
    public boolean sendMessage = true;

    @Override
    public TaskType getType() {
        return TaskTypes.ONE_TIME;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isCompleted(PlayerData data, QuestLine line, Quest quest) {
        if (PixelBuiltQuests.getStorage().hasRan(data.id, line, quest.getId())) {
            if (sendMessage)
                data.getUser().getPlayer().ifPresent(p ->
                        p.sendMessage(Util.toText(ConfigManager.getConfig().messages.hasRan))
                );
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Quest quest, QuestLine line, int questId) {
        PixelBuiltQuests.getStorage().run(player.getUniqueId(), line, questId);
    }
}
