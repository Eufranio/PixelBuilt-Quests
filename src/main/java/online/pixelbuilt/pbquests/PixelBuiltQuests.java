package online.pixelbuilt.pbquests;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import online.pixelbuilt.pbquests.config.*;
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
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
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

    private Config<ConfigCategory> mainConfig;
    private StorageModule storage;

    @Inject
    @ConfigDir(sharedRoot = false)
    public File configDir;

    @Inject
    public Logger logger;

    @Listener
    public void onInit(GamePostInitializationEvent e) {
        instance = this;
        this.mainConfig  = new Config<>(ConfigCategory.class, "PBQ.conf");
        this.initStorage();


        logger.warn("PixelBuilt - Quests is starting!");
        CommandManager.registerCommands();
        Sponge.getEventManager().registerListeners(this, new Listeners());

        Sponge.getRegistry().registerModule(TaskType.class, new TaskRegistryModule());
        Sponge.getRegistry().registerModule(RewardType.class, new RewardRegistryModule());
    }

    @Listener
    public void onReload(GameReloadEvent e) {
        this.mainConfig.reload();
        this.initStorage();
    }

    private void initStorage() {
        switch (mainConfig.get().storage) {
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

    public static ConfigCategory getConfig() {
        return instance.mainConfig.get();
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
