package online.pixelbuilt.pbquests;

import com.google.inject.Inject;
import online.pixelbuilt.pbquests.persistence.QuestDAO;
import online.pixelbuilt.pbquests.persistence.QuestPersistenceService;
import online.pixelbuilt.pbquests.utils.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
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
    public static Config config;
    public static Set<UUID> playersBusy;
    public static Map<UUID, Quest> runningQuests;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path configFile;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    public PixelBuiltQuests(Logger logger) {
        this.logger = logger;
    }

    public static PixelBuiltQuests getInstance() {
        return instance;
    }

    @Listener
    public void onInit(GamePostInitializationEvent e) {
        logger.warn("PixelBuilt - Quests is starting!");
        instance = this;
        config = new Config(this, configFile, configDir);
        config.load();
        QuestPersistenceService db = new QuestPersistenceService(new QuestDAO());
        Sponge.getServiceManager().setProvider(this, QuestPersistenceService.class, db);

        QuestService questService = new QuestService();
        questService.initialize();

        Command.registerCommand();
        Sponge.getEventManager().registerListeners(this, new Listeners());
        Sponge.getEventManager().registerListeners(this, new ChatUtils());
        playersBusy = new HashSet<>();
        runningQuests = new HashMap<>();
    }

    @Listener
    public void onStop(GameStoppingEvent e) {
        config = null;
    }

    @Listener
    public void onReload(GameReloadEvent e) {
        config = null;
        config = new Config(this, configFile, configDir);
        config.load();
    }

}
