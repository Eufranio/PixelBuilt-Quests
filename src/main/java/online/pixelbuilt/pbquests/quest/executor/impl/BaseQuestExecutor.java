package online.pixelbuilt.pbquests.quest.executor.impl;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.config.serialization.ValueWrapper;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutor;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frani on 20/01/2019.
 */
public class BaseQuestExecutor implements QuestExecutor {

    private Quest quest;
    private QuestLine questLine;
    private int questId;
    private UUID player;

    public BaseQuestExecutor() {

    }

    private BaseQuestExecutor(Quest quest, QuestLine questLine, int questId, UUID player) {
        this.quest = quest;
        this.questLine = questLine;
        this.questId = questId;
        this.player = player;
    }

    @Override
    public void execute(Quest quest, QuestLine questLine, int questId, Player player) {
        new BaseQuestExecutor(quest, questLine, questId, player.getUniqueId()).run();
    }

    public void run() {
        Player player = this.getPlayer();
        for (ValueWrapper<? extends BaseTask> v : this.quest.tasks) {
            BaseTask task = v.getValue();
            if (!task.check(player, this.quest, this.questLine, this.questId)) {
                return;
            }
        }

        for (ValueWrapper<? extends BaseTask> v : this.quest.tasks) {
            BaseTask task = v.getValue();
            task.complete(player, this.quest, this.questLine, this.questId);
        }

        PixelBuiltQuests.runningQuests.add(player.getUniqueId());

        // Add player to the list of busy players
        if (quest.denyMovement) {
            if (!PixelBuiltQuests.playersBusy.contains(player.getUniqueId()))
                PixelBuiltQuests.playersBusy.add(player.getUniqueId());
        }

        if (quest.timeBetweenMessages == 0) {
            for (String s : quest.messages) {
                player.sendMessage(Util.toText(s.replace("%player%", player.getName())));
            }
            this.continueTask();
        } else {
            int count = 0;
            if (quest.messages.isEmpty()) continueTask();
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

        if (quest.denyMovement) {
            PixelBuiltQuests.playersBusy.remove(player.getUniqueId());
        }
        PixelBuiltQuests.runningQuests.remove(player.getUniqueId());

        for (ValueWrapper<? extends BaseReward> v : this.quest.rewards) {
            BaseReward reward = v.getValue();
            reward.execute(player, quest, questLine, questId);
        }

        String finish = ConfigManager.getConfig().messages.finish;
        if (!finish.isEmpty() && !quest.displayName.isEmpty()) {
            player.sendMessage(Util.toText(finish.replace("%quest%", quest.displayName)));
        }
    }

    private Player getPlayer() {
        return Sponge.getServer().getPlayer(this.player).orElse(null);
    }

}
