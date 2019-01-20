package online.pixelbuilt.pbquests.quest;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.reward.RewardType;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frani on 20/01/2019.
 */
public class QuestExecutor {

    private Quest quest;
    private QuestLine questLine;
    private int questId;
    private UUID player;

    public QuestExecutor(Quest quest, QuestLine questLine, int questId, UUID player) {
        this.quest = quest;
        this.questLine = questLine;
        this.questId = questId;
        this.player = player;
    }

    public void run() {
        Player player = this.getPlayer();
        for (Map.Entry<TaskType, Map<String, String>> entry : this.quest.tasks.entrySet()) {
            BaseTask task = ConfigManager.getMapping(entry.getKey().getCatalogClass());
            if (task != null) {
                if (!task.complete(entry.getValue(), player, this.quest, this.questLine, this.questId)) {
                    return;
                }
            }
        }

        PixelBuiltQuests.runningQuests.add(player.getUniqueId());

        // Add player to the list of busy players
        if (quest.denyMovement) {
            if (PixelBuiltQuests.playersBusy.contains(player.getUniqueId())) return;
            PixelBuiltQuests.playersBusy.add(player.getUniqueId());
        }

        if (quest.timeBetweenMessages == 0) {
            for (String s : quest.messages) {
                player.sendMessage(Util.toText(s.replace("%player%", player.getName())));
            }
            this.continueTask();
        } else {
            int count = 0;
            for (String msg : quest.messages) {
                count += quest.timeBetweenMessages;
                final int currentCount = count;
                Task.builder()
                        .delay(count, TimeUnit.SECONDS)
                        .execute(() -> {
                            Player pl = this.getPlayer();
                            if (pl != null) {
                                pl.sendMessage(Util.toText(msg.replace("%player%", pl.getName())));
                            }
                            if (currentCount == quest.messages.size()) {
                                Task.builder()
                                        .delay(quest.timeBetweenMessages, TimeUnit.SECONDS)
                                        .execute(this::continueTask)
                                        .submit(PixelBuiltQuests.instance);
                            }
                        })
                        .submit(PixelBuiltQuests.instance);
            }
        }
    }

    public void continueTask() {
        Player player = this.getPlayer();
        if (player == null) return;

        for (Map.Entry<RewardType, Map<String, String>> entry : this.quest.rewards.entrySet()) {
            BaseReward reward = ConfigManager.getMapping(entry.getKey().getCatalogClass());
            if (reward != null) {
                reward.execute(player, entry.getValue(), quest, questLine, questId);
            }
        }

        if (quest.denyMovement) {
            PixelBuiltQuests.playersBusy.remove(player.getUniqueId());
        }
        PixelBuiltQuests.runningQuests.remove(player.getUniqueId());

        String finish = PixelBuiltQuests.getConfig().messages.finish;
        if (!finish.isEmpty()) {
            player.sendMessage(Util.toText(finish.replace("%quest%", questLine.getName())));
        }
    }

    private Player getPlayer() {
        return Sponge.getServer().getPlayer(this.player).orElse(null);
    }

}
