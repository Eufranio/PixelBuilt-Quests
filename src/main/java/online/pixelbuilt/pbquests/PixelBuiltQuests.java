package online.pixelbuilt.pbquests;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import online.pixelbuilt.pbquests.config.*;
import online.pixelbuilt.pbquests.storage.FileStorage;
import online.pixelbuilt.pbquests.storage.SQLStorage;
import online.pixelbuilt.pbquests.storage.StorageModule;
import online.pixelbuilt.pbquests.utils.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.util.*;

/**
 * Created by Frani on 05/09/2017.
 */

@Plugin(name = "PixelBuiltQuests",
        id = "pixelbuilt-quests",
        authors = { "Eufranio" })
public class PixelBuiltQuests {

    public static Logger logger;
    public static PixelBuiltQuests instance = null;
    public static List<UUID> playersBusy = Lists.newArrayList();
    public static List<UUID> runningQuests = Lists.newArrayList();

    private Config<ConfigCategory> mainConfig = new Config<>(ConfigCategory.class, "PBQ.conf");
    private StorageModule storage;

    @Inject
    @ConfigDir(sharedRoot = false)
    public File configDir;

    @Inject
    public PixelBuiltQuests(Logger l) {
        logger = l;
    }

    @Listener
    public void onInit(GamePostInitializationEvent e) {
        instance = this;

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

        logger.warn("PixelBuilt - Quests is starting!");
        Command.registerCommand();
        Sponge.getEventManager().registerListeners(this, new Listeners());
        Sponge.getEventManager().registerListeners(this, new ChatUtils());
    }

    @Listener
    public void onReload(GameReloadEvent e) {
        this.mainConfig.reload();
    }

    public static ConfigCategory getConfig() {
        return instance.mainConfig.get();
    }

    public static StorageModule getStorage() {
        return instance.storage;
    }

}
