package dev.eufranio.pixelbuiltquests.quest.executor.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.events.internal.PBQInternalEvents;
import dev.eufranio.pixelbuiltquests.listeners.ListenerProvider;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.quest.executor.QuestExecutor;
import dev.eufranio.pixelbuiltquests.reward.BaseReward;
import dev.eufranio.pixelbuiltquests.storage.sql.TaskStatus;
import dev.eufranio.pixelbuiltquests.task.AmountTask;
import dev.eufranio.pixelbuiltquests.task.BaseTask;
import dev.eufranio.pixelbuiltquests.task.QuestTaskReference;
import dev.eufranio.pixelbuiltquests.task.impl.ProgressRequiredTask;
import dev.eufranio.pixelbuiltquests.utils.SchedulerUtil;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class BaseQuestExecutor implements QuestExecutor, ListenerProvider {

    static final Set<UUID> preventMovement = Sets.newHashSet();

    static final PBQInternalEvents.MoveEntity consumer = (entity, world, from, to) -> {
        if (preventMovement.contains(entity.getUUID())) {
            return EventResult.interruptDefault();
        }
        return EventResult.pass();
    };

    static final PlayerEvent.PlayerQuit quit = player -> {
        preventMovement.remove(player.getUUID());
    };

    @Override
    public void registerListeners() {
        PlayerEvent.PLAYER_QUIT.register(quit);
        PBQInternalEvents.MOVE_ENTITY.register(consumer);
    }

    @Override
    public void unregisterListeners() {
        PlayerEvent.PLAYER_QUIT.unregister(quit);
        PBQInternalEvents.MOVE_ENTITY.unregister(consumer);
    }

    @Override
    public void execute(ServerPlayer player, QuestReference reference, boolean sendIncompleteTasksMessage) {
        if (PixelBuiltQuests.storage().hasStarted(player.getUUID(), reference)) {
            this.run(player, reference, sendIncompleteTasksMessage);
        } else {
            if (this.start(player, reference, sendIncompleteTasksMessage) && reference.getQuest().runUponStart)
                this.run(player, reference, sendIncompleteTasksMessage);
        }
    }

    private boolean start(ServerPlayer player, QuestReference reference, boolean sendIncompleteTasksMessage) {
        if (!reference.getQuest().repeatable && PixelBuiltQuests.storage().hasCompleted(player.getUUID(), reference)) {
            player.sendSystemMessage(Util.text(ConfigManager.getConfig().messages.hasRan));
            return false;
        }

        if (reference.getQuest().cooldown) {
            Duration cooldown = Duration.parse("PT" + reference.getQuest().cooldownDuration);
            Date lastRan = PixelBuiltQuests.storage().getLastStartedOrCompleted(player.getUUID(), reference);
            if (lastRan != null) {
                Instant nextRun = lastRan.toInstant().plusSeconds(cooldown.getSeconds());
                if (Instant.now().isBefore(nextRun)) {
                    long seconds = Instant.now().until(nextRun, ChronoUnit.SECONDS);
                    String cooldownMessage = ConfigManager.getConfig().messages.cooldown
                            .replace("%cooldown%", Util.timeDiffFormat(seconds, true));
                    player.sendSystemMessage(Util.text(cooldownMessage));
                    return false;
                }
            }
        }

        reference.getQuest().startMessages.forEach(str ->
                player.sendSystemMessage(Util.text(str.replace("%player%", player.getName().getString()))));
        PixelBuiltQuests.storage().startQuest(player.getUUID(), reference);

        return true;
    }

    public void run(ServerPlayer player, QuestReference reference, boolean sendIncompleteTasksMessage) {
        List<MutableComponent> toComplete = Lists.newArrayList();
        for (BaseTask task : reference.getQuest().getTasks()) {
            if (!task.isCompleted(player.getUUID(), reference)) {
                if (task instanceof AmountTask amountTask) {
                    TaskStatus status = PixelBuiltQuests.storage().getOrCreateTaskStatus(player.getUUID(), QuestTaskReference.of(reference, amountTask));
                    amountTask.tryIncrease(player.getUUID(), status);
                }

                // task is still incomplete even after increasing
                if (task instanceof ProgressRequiredTask || !task.isCompleted(player.getUUID(), reference))
                    toComplete.add(task.toText());
            }
        }

        if (!toComplete.isEmpty()) {
            MutableComponent separator = Component.literal(", ");
            MutableComponent list = toComplete.stream()
                    .flatMap(task -> Stream.of(separator, task))
                    .skip(1)
                    .reduce(Component.empty(), MutableComponent::append);
            if (sendIncompleteTasksMessage) {
                MutableComponent component = Component.empty()
                        .append(Util.text(ConfigManager.getConfig().messages.notAllTasksCompleted))
                        .append(list);
                player.sendSystemMessage(component);
                String hintText = ConfigManager.getConfig().messages.notAllTasksCompletedHint;
                if (!hintText.isEmpty())
                    player.sendSystemMessage(Util.text(hintText));
            }
            return;
        }

        PixelBuiltQuests.setRunningQuest(player.getUUID(), true);

        // Add player to the list of busy players
        if (reference.getQuest().denyMovement) {
            preventMovement.add(player.getUUID());
        }

        if (reference.getQuest().timeBetweenMessages == 0 || reference.getQuest().messages.isEmpty()) {
            reference.getQuest().messages.forEach(str ->
                    player.sendSystemMessage(Util.text(str.replace("%player%", player.getName().getString()))));
            this.continueTask(player.getUUID(), reference);
        } else {
            final Deque<String> messages = new ConcurrentLinkedDeque<>(reference.getQuest().messages);
            final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(() -> {
                String message = messages.poll();
                if (message == null) {
                    SchedulerUtil.sync().execute(() -> {
                        continueTask(player.getUUID(), reference);
                    });
                    scheduler.shutdown();
                    return;
                }
                SchedulerUtil.sync().execute(() -> {
                    final ServerPlayer serverPlayer = Util.player(player.getUUID());
                    if (serverPlayer != null)
                        serverPlayer.sendSystemMessage(Util.text(message
                                .replace("%player%", player.getName().getString())));
                });
            }, 0, reference.getQuest().timeBetweenMessages, TimeUnit.SECONDS);
        }
    }

    private void continueTask(UUID uuid, QuestReference reference) {
        // those don't need the player online to execute
        PixelBuiltQuests.setRunningQuest(uuid, false);
        preventMovement.remove(uuid);

        final ServerPlayer player = Util.player(uuid);
        if (player == null)
            return;

        for (BaseReward reward : reference.getQuest().getRewards()) {
            reward.execute(uuid, reference);
        }

        PixelBuiltQuests.storage().saveCompletedQuest(player.getUUID(), reference);
    }

}
