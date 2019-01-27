package online.pixelbuilt.pbquests;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import online.pixelbuilt.pbquests.config.*;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutorType;
import online.pixelbuilt.pbquests.quest.executor.QuestExecutorTypeRegistryModule;
import online.pixelbuilt.pbquests.reward.RewardRegistryModule;
import online.pixelbuilt.pbquests.reward.RewardType;
import online.pixelbuilt.pbquests.storage.FileStorage;
import online.pixelbuilt.pbquests.storage.SQLStorage;
import online.pixelbuilt.pbquests.storage.StorageModule;
import online.pixelbuilt.pbquests.task.TaskRegistryModule;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.utils.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.util.*;

/**
 * Created by Frani on 05/09/2017.
 */

@Plugin(name = "PixelBuiltQuests",
        description = "Quests plugin made for the PixelBuilt server",
        id = "pixelbuilt-quests",
        authors = { "Eufranio" })
public class PixelBuiltQuests {

    public static PixelBuiltQuests instance = null;
    public static List<UUID> playersBusy = Lists.newArrayList();
    public static List<UUID> runningQuests = Lists.newArrayList();

    private StorageModule storage;

    @Inject
    @ConfigDir(sharedRoot = false)
    public File configDir;

    @Inject
    public Logger logger;

    @Listener
    public void onPreInit(GamePreInitializationEvent e) {
        instance = this;

        Sponge.getRegistry().registerModule(TaskType.class, new TaskRegistryModule());
        Sponge.getRegistry().registerModule(RewardType.class, new RewardRegistryModule());
        Sponge.getRegistry().registerModule(QuestExecutorType.class, new QuestExecutorTypeRegistryModule());

        ConfigManager.init();
        this.initStorage();

        logger.warn("PixelBuilt - Quests is starting!");
        CommandManager.registerCommands();
        Sponge.getEventManager().registerListeners(this, new Listeners());
    }

    @Listener
    public void onInit(GamePostInitializationEvent e) {
        ConfigManager.loadCatalogs();
    }

    @Listener
    public void onReload(GameReloadEvent e) {
        ConfigManager.reload();
        this.storage.shutdown();
        this.initStorage();
    }

    private void initStorage() {
        switch (ConfigManager.getConfig().storage) {
            case 1:
                this.storage = new FileStorage();
                break;
            case 2:
                this.storage = new SQLStorage();
                break;
            default:
                this.storage = new FileStorage();
        }

        this.storage.init(this);
    }

    public static StorageModule getStorage() {
        return instance.storage;
    }

    public static PixelBuiltQuests getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return this.logger;
    }

}
