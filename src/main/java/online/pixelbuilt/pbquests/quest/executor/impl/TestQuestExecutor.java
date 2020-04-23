package online.pixelbuilt.pbquests.quest.executor.impl;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.config.serialization.ValueWrapper;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutor;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.storage.SQLStorage;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.AmountTask;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TestQuestExecutor implements QuestExecutor {

    private Quest quest;
    private QuestLine questLine;
    private UUID player;

    @Override
    public void execute(Quest quest, QuestLine questLine, Player player) {
        this.quest = quest;
        this.questLine = questLine;
        this.player = player.getUniqueId();
        this.run();
    }

    public void run() {
        Player player = this.getPlayer();
        PlayerData playerData = ((SQLStorage) PixelBuiltQuests.getStorage()).getData(this.player);

        boolean canComplete = true;
        for (ValueWrapper<? extends BaseTask> v : this.quest.tasks) {
            BaseTask task = v.getValue();
            QuestStatus status = playerData.getStatus(task, questLine, quest);
            if (task instanceof AmountTask)
                ((AmountTask) task).tryIncrease(playerData, status);

            if (!task.isCompleted(playerData, questLine, quest)) {
                player.sendMessage(Text.of(
                        "You must complete this ", task.getType().getName(), " task before complete the quest! ",
                        task instanceof AmountTask ?
                                Text.of("Progress: ",
                                        playerData.getProgress(task, this.questLine, this.quest),
                                        "/",
                                        ((AmountTask) task).getTotal()
                                ) : Text.of()
                ));
                canComplete = false;
            }
        }

        if (!canComplete)
            return;

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
            reward.execute(player, quest, questLine, quest.getId());
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
