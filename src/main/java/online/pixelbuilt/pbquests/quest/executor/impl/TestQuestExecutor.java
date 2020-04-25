package online.pixelbuilt.pbquests.quest.executor.impl;

import com.google.common.collect.Lists;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.config.serialization.ValueWrapper;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutor;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.storage.StorageManager;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.AmountTask;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
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
        PlayerData playerData = PixelBuiltQuests.getStorage().getData(this.player);

        if (!quest.repeatable && playerData.hasRan(questLine, quest)) {
            player.sendMessage(Util.toText(ConfigManager.getConfig().messages.hasRan));
            return;
        }

        if (!playerData.startedQuests.contains(this.questLine.getName() + "," + this.quest.getId())) {
            playerData.startedQuests.add(this.questLine.getName() + "," + this.quest.getId());
            ((StorageManager) PixelBuiltQuests.getStorage()).save(playerData);
        }

        boolean canComplete = true;
        List<Text> toComplete = Lists.newArrayList();
        for (ValueWrapper<? extends BaseTask> v : this.quest.tasks) {
            BaseTask task = v.getValue();
            QuestStatus status = playerData.getStatus(task, questLine, quest);
            if (task instanceof AmountTask)
                ((AmountTask) task).tryIncrease(playerData, status);

            if (!task.isCompleted(playerData, questLine, quest)) {
                toComplete.add(task.getDisplay());
            }
        }

        if (!toComplete.isEmpty()) {
            player.sendMessage(Text.of(
                    TextColors.RED, "There are tasks that you haven't completed yet for this Quest: ",
                    Text.joinWith(Text.of(", "), toComplete)
            ));
            player.sendMessage(Text.of(
                    TextColors.AQUA, "Hint: ",
                    TextColors.RED, "Use /pbq status to check your current status on those tasks!"
            ));
            return;
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

        PlayerData playerData = ((StorageManager) PixelBuiltQuests.getStorage()).getData(this.player);

        if (quest.denyMovement) {
            PixelBuiltQuests.playersBusy.remove(player.getUniqueId());
        }
        PixelBuiltQuests.runningQuests.remove(player.getUniqueId());

        for (ValueWrapper<? extends BaseTask> v : this.quest.tasks) {
            BaseTask task = v.getValue();
            QuestStatus status = playerData.getStatus(task, questLine, quest);
            if (status != null) {
                playerData.status.remove(status);
            }
        }

        for (ValueWrapper<? extends BaseReward> v : this.quest.rewards) {
            BaseReward reward = v.getValue();
            reward.execute(player, quest, questLine, quest.getId());
        }

        String finish = ConfigManager.getConfig().messages.finish;
        if (!finish.isEmpty() && !quest.displayName.isEmpty()) {
            player.sendMessage(Util.toText(finish.replace("%quest%", quest.displayName)));
        }

        playerData.startedQuests.remove(this.questLine.getName() + "," + this.quest.getId());
        playerData.addQuest(this.questLine, this.quest);
        PixelBuiltQuests.getStorage().save(playerData);
    }

    private Player getPlayer() {
        return Sponge.getServer().getPlayer(this.player).orElse(null);
    }
}
