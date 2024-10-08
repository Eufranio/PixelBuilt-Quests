package dev.eufranio.pixelbuiltquests.storage;

import com.google.common.collect.Maps;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.storage.sql.*;
import dev.eufranio.pixelbuiltquests.task.BaseTask;
import dev.eufranio.pixelbuiltquests.task.QuestTaskReference;
import io.github.eufranio.storage.Persistable;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;

public class StorageManager {

    final Map<UUID, PlayerData> cachedPlayers = Maps.newHashMap();

    private Persistable<PlayerData, UUID> players;
    private Persistable<QuestLineProgress, Integer> progress;
    private Persistable<StartedQuest, Integer> questsStarted;
    private Persistable<CompletedQuest, Integer> questsCompleted;
    private Persistable<TaskStatus, Integer> taskStatus;

    public void init() {
        String url = ConfigManager.getConfig().database.url;

        players = Persistable.create(PlayerData.class, url);
        progress = Persistable.create(QuestLineProgress.class, url);
        questsStarted = Persistable.create(StartedQuest.class, url);
        questsCompleted = Persistable.create(CompletedQuest.class, url);
        taskStatus = Persistable.create(TaskStatus.class, url);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.cachedPlayers.computeIfAbsent(uuid, u -> {
            PlayerData data = this.players.getOrCreate(uuid);
            data.refreshData();
            return data;
        });
    }

    private void removeStartedQuest(UUID player, QuestReference reference) {
        PlayerData data = this.getPlayerData(player);
        data.getQuestsStarted().removeIf(s ->
                s.getQuestLine().equals(reference.getQuestLine().getId()) &&
                        s.getQuestId().equals(reference.getQuest().getId()));
    }

    public void saveCompletedQuest(UUID player, QuestReference reference) {
        removeStartedQuest(player, reference);

        // clear the previous progress of this quest, so it can be restarted
        clearTaskStatus(player, reference);

        PlayerData data = getPlayerData(player);
        CompletedQuest completed = new CompletedQuest(data, reference, new Date());
        data.getQuestsCompleted().add(completed);
    }

    private void clearTaskStatus(UUID player, QuestReference reference) {
        PlayerData data = getPlayerData(player);
        data.getStatus().removeIf(s ->
                s.getQuestLine().equals(reference.getQuestLine().getId()) &&
                        s.getQuestId().equals(reference.getQuest().getId()));
    }

    public boolean hasCompleted(UUID player, QuestReference reference) {
        PlayerData data = getPlayerData(player);
        return data.getQuestsCompleted().stream()
                .anyMatch(s ->
                        s.getQuestLine().equals(reference.getQuestLine().getId()) &&
                        s.getQuestId().equals(reference.getQuest().getId()));
    }

    public boolean hasStarted(UUID player, QuestReference reference) {
        PlayerData data = getPlayerData(player);
        return data.getQuestsStarted().stream()
                .anyMatch(s ->
                        s.getQuestLine().equals(reference.getQuestLine().getId()) &&
                        s.getQuestId().equals(reference.getQuest().getId()));
    }

    public void resetQuestLine(UUID player, QuestLine line) {
        // deletes all started/completed quests from the collection and from the db
        PlayerData data = getPlayerData(player);
        data.getQuestsStarted().removeIf(s -> s.getQuestLine().equals(line.getId()));
        data.getQuestsCompleted().removeIf(s -> s.getQuestLine().equals(line.getId()));
    }

    public void startQuest(UUID player, QuestReference reference) {
        if (!hasStarted(player, reference)) {
            PlayerData data = getPlayerData(player);
            StartedQuest started = new StartedQuest(data, reference);
            data.getQuestsStarted().add(started);
        }
    }

    public int getProgress(UUID player, QuestLine line) {
        PlayerData data = getPlayerData(player);
        return data.getProgress().stream()
                .filter(p -> p.getQuestLine().equals(line.getId()))
                .findFirst()
                .map(QuestLineProgress::getProgress)
                .orElse(0);
    }

    public void addProgress(UUID player, QuestLine line, int progress) {
        PlayerData data = getPlayerData(player);
        QuestLineProgress object = data.getProgress().stream()
                .filter(p -> p.getQuestLine().equals(line.getId()))
                .findFirst()
                .orElse(null);
        if (object == null) {
            object = new QuestLineProgress(data, line, progress);
            data.getProgress().add(object);
        } else {
            object.setProgress(object.getProgress() + progress);
            try {
                object.update();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setProgress(UUID player, QuestLine line, int progress) {
        PlayerData data = getPlayerData(player);
        QuestLineProgress object = data.getProgress().stream()
                .filter(p -> p.getQuestLine().equals(line.getId()))
                .findFirst()
                .orElse(null);
        if (object == null) {
            object = new QuestLineProgress(data, line, progress);
            data.getProgress().add(object);
        } else {
            object.setProgress(progress);
            try {
                object.update();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public @Nullable Date getLastStartedOrCompleted(UUID player, QuestReference reference) {
        PlayerData data = getPlayerData(player);
        if (hasStarted(player, reference)) {
            return data.getQuestsStarted().stream()
                    .filter(s -> s.getQuestLine().equals(reference.getQuestLine().getId()) && s.getQuestId().equals(reference.getQuest().getId()))
                    .max(Comparator.comparing(StartedQuest::getDateStarted))
                    .map(StartedQuest::getDateStarted)
                    .orElse(null);
        } else if (hasCompleted(player, reference)) {
            return data.getQuestsCompleted().stream()
                    .filter(s -> s.getQuestLine().equals(reference.getQuestLine().getId()) && s.getQuestId().equals(reference.getQuest().getId()))
                    .max(Comparator.comparing(CompletedQuest::getDateCompleted))
                    .map(CompletedQuest::getDateCompleted)
                    .orElse(null);
        }
        return null;
    }

    public TaskStatus getOrCreateTaskStatus(UUID player, QuestTaskReference taskReference) {
        PlayerData data = getPlayerData(player);
        TaskStatus status = data.getStatus().stream()
                .filter(s -> s.getQuestLine().equals(taskReference.getQuestLine().getId()) &&
                        s.getQuestId().equals(taskReference.getQuest().getId()) &&
                        s.getTaskId().equals(taskReference.getTask().getId()))
                .findFirst()
                .orElse(null);

        if (status != null) {
            return status;
        }

        status = new TaskStatus(data, taskReference);
        data.getStatus().add(status);
        return status;
    }

    public Optional<TaskStatus> getStatus(UUID player, QuestTaskReference taskReference) {
        if (!this.hasStarted(player, taskReference))
            return Optional.empty();
        return Optional.of(this.getOrCreateTaskStatus(player, taskReference));
    }

    public Persistable<PlayerData, UUID> playerDataDao() {
        return this.players;
    }

    public Persistable<QuestLineProgress, Integer> questLineProgressDao() {
        return this.progress;
    }

    public Persistable<StartedQuest, Integer> startedQuestDao() {
        return this.questsStarted;
    }

    public Persistable<TaskStatus, Integer> taskStatusDao() {
        return this.taskStatus;
    }

    public Persistable<CompletedQuest, Integer> completedQuestDao() {
        return this.questsCompleted;
    }

}
