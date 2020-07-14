package online.pixelbuilt.pbquests.quest.executor.impl;

import com.google.common.collect.Lists;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.config.serialization.ValueWrapper;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutor;
import online.pixelbuilt.pbquests.reward.BaseReward;
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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseQuestExecutor implements QuestExecutor {

    UUID player;
    Quest quest;
    QuestLine questLine;
    PlayerData playerData;

    @Override
    public void execute(Quest quest, QuestLine questLine, Player player) {
        this.quest = quest;
        this.questLine = questLine;
        this.player = player.getUniqueId();
        this.playerData = PixelBuiltQuests.getStorage().getData(this.player);

        if (playerData.hasStarted(questLine, quest)) {
            this.run();
        } else {
            if (this.start() && quest.runUponStart)
                this.run();
        }
    }

    public boolean start() {
        Player player = this.getPlayer();
        if (!quest.repeatable && playerData.hasRan(questLine, quest)) {
            player.sendMessage(Util.toText(ConfigManager.getConfig().messages.hasRan));
            return false;
        }

        if (quest.cooldown) {
            Duration cooldown = Duration.parse("PT" + quest.cooldownDuration);
            Instant lastRan = playerData.getLastRan(questLine, quest);
            if (lastRan != null) {
                Instant nextRun = lastRan.plusSeconds(cooldown.getSeconds());
                if (Instant.now().isBefore(nextRun)) {
                    long seconds = Instant.now().until(nextRun, ChronoUnit.SECONDS);
                    String cooldownMessage = ConfigManager.getConfig().messages.cooldown
                            .replace("%cooldown%", Util.timeDiffFormat(seconds, true));
                    player.sendMessage(Util.toText(cooldownMessage));
                    return false;
                }
            }
        }

        quest.startMessages.forEach(str -> player.sendMessage(Util.toText(str.replace("%player%", player.getName()))));
        playerData.startQuest(questLine, quest);
        PixelBuiltQuests.getStorage().save(playerData);

        return true;
    }

    public void run() {
        List<Text> toComplete = Lists.newArrayList();
        for (ValueWrapper<? extends BaseTask> v : this.quest.tasks) {
            BaseTask task = v.getValue();
            QuestStatus status = playerData.getStatus(task, questLine, quest);

            if (!task.isCompleted(playerData, questLine, quest)) {
                if (task instanceof AmountTask)
                    ((AmountTask) task).tryIncrease(playerData, status);

                if (!task.isCompleted(playerData, questLine, quest))
                    toComplete.add(task.toText());
            }
        }

        final Player player = this.getPlayer();
        if (!toComplete.isEmpty()) {
            player.sendMessage(Text.of(Util.toText(ConfigManager.getConfig().messages.notAllTasksCompleted), Text.joinWith(Text.of(", "), toComplete)));
            String hintText = ConfigManager.getConfig().messages.notAllTasksCompletedHint;
            if (!hintText.isEmpty())
                player.sendMessage(Util.toText(hintText));
            return;
        }

        PixelBuiltQuests.runningQuests.add(player.getUniqueId());

        // Add player to the list of busy players
        if (quest.denyMovement) {
            if (!PixelBuiltQuests.playersBusy.contains(player.getUniqueId()))
                PixelBuiltQuests.playersBusy.add(player.getUniqueId());
        }

        if (quest.timeBetweenMessages == 0 || quest.messages.isEmpty()) {
            quest.messages.forEach(str -> player.sendMessage(Util.toText(str.replace("%player%", player.getName()))));
            this.continueTask();
        } else {
            AtomicInteger count = new AtomicInteger();
            for (int i = 0; i < quest.messages.size(); i++) {
                final int j = i;
                Task.builder()
                        .delay(count.getAndAdd(quest.timeBetweenMessages), TimeUnit.SECONDS)
                        .execute(() -> {
                            Player pl = this.getPlayer();
                            if (pl != null) {
                                pl.sendMessage(Util.toText(quest.messages.get(j).replace("%player%", pl.getName())));
                            }
                            if (j == quest.messages.size() - 1) {
                                Task.builder()
                                        .delay(quest.timeBetweenMessages, TimeUnit.SECONDS)
                                        .execute(this::continueTask)
                                        .submit(PixelBuiltQuests.getInstance());
                            }
                        })
                        .submit(PixelBuiltQuests.getInstance());
            }
        }
    }

    public void continueTask() {
        // those don't need the player online to execute
        PixelBuiltQuests.playersBusy.remove(player);
        PixelBuiltQuests.runningQuests.remove(player);

        Player player = this.getPlayer();
        if (player == null) return;

        // refresh data that might have been changed since last query
        playerData.refreshData();

        for (ValueWrapper<? extends BaseTask> v : this.quest.tasks) {
            BaseTask task = v.getValue();
            QuestStatus status = playerData.getStatus(task, questLine, quest);
            if (status != null) {
                playerData.status.remove(status);
                // clear the previous progress of this quest, so it can be restarted
            }
        }

        for (ValueWrapper<? extends BaseReward<?>> v : this.quest.rewards) {
            BaseReward<?> reward = v.getValue();
            reward.execute(playerData, questLine, quest);
        }

        playerData.startedQuests.remove(this.questLine.getName() + "," + this.quest.getId());
        playerData.addQuest(this.questLine, this.quest);
        PixelBuiltQuests.getStorage().save(playerData);
    }

    private Player getPlayer() {
        return Sponge.getServer().getPlayer(this.player).orElse(null);
    }
}
