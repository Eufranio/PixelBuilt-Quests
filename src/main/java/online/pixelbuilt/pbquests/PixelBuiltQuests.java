package online.pixelbuilt.pbquests;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import online.pixelbuilt.pbquests.config.*;
import online.pixelbuilt.pbquests.config.serialization.ValueWrapper;
import online.pixelbuilt.pbquests.listeners.Listeners;
import online.pixelbuilt.pbquests.listeners.TaskListener;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutorType;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutorTypeRegistryModule;
import online.pixelbuilt.pbquests.reward.RewardRegistryModule;
import online.pixelbuilt.pbquests.reward.RewardType;
import online.pixelbuilt.pbquests.storage.StorageManager;
import online.pixelbuilt.pbquests.task.TaskRegistryModule;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.impl.ByteItemTask;
import online.pixelbuilt.pbquests.utils.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.util.*;

/**
 * Created by Frani on 05/09/2017.
 */

@Plugin(name = "PixelBuiltQuests",
        description = "Quests plugin made for the PixelBuilt server",
        id = "pixelbuilt-quests",
        authors = { "Eufranio" },
        dependencies = @Dependency(id = "byte-items", optional = true)
)
public class PixelBuiltQuests {

    public static PixelBuiltQuests instance = null;
    public static List<UUID> playersBusy = Lists.newArrayList();
    public static List<UUID> runningQuests = Lists.newArrayList();

    private StorageManager storage;

    @Inject
    @ConfigDir(sharedRoot = false)
    public File configDir;

    @Inject
    public Logger logger;

    TaskListener taskListener;

    @Listener
    public void onPreInit(GamePreInitializationEvent e) {
        instance = this;

        Sponge.getRegistry().registerModule(TaskType.class, new TaskRegistryModule());
        Sponge.getRegistry().registerModule(RewardType.class, new RewardRegistryModule());
        Sponge.getRegistry().registerModule(QuestExecutorType.class, new QuestExecutorTypeRegistryModule());

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ValueWrapper.class), new ValueWrapper.ValueWrapperTypeSerializer());
    }

    @Listener
    public void onStarted(GameStartedServerEvent event) {
        ConfigManager.init();

        this.taskListener = new TaskListener(this);
        this.taskListener.reloadEvents();
        Sponge.getEventManager().registerListeners(this, taskListener);

        this.storage = new StorageManager(this);
        this.storage.init();

        logger.warn("PixelBuilt - Quests is starting!");
        CommandManager.registerCommands();
        Sponge.getEventManager().registerListeners(this, new Listeners());
    }

    @Listener
    public void onRegisterTask(GameRegistryEvent.Register<TaskType> event) {
        if (Sponge.getPluginManager().isLoaded("byte-items")) {
            event.register(ByteItemTask.TASK_TYPE);
        }
    }

    @Listener
    public void onInit(GamePostInitializationEvent e) {
        ConfigManager.loadCatalogs();
    }

    @Listener
    public void onReload(GameReloadEvent e) {
        ConfigManager.reload();
        this.taskListener.reloadEvents();
    }

    public static StorageManager getStorage() {
        return instance.storage;
    }

    public static PixelBuiltQuests getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return this.logger;
    }

}
