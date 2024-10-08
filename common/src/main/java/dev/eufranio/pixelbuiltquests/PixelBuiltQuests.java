package dev.eufranio.pixelbuiltquests;

import com.google.common.collect.Lists;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.eufranio.pixelbuiltquests.command.PBQCommand;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.events.PBQEvents;
import dev.eufranio.pixelbuiltquests.placeholder.PlaceholderHandler;
import dev.eufranio.pixelbuiltquests.quest.Quest;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import dev.eufranio.pixelbuiltquests.quest.QuestLineRegistryModule;
import dev.eufranio.pixelbuiltquests.quest.QuestRegistryModule;
import dev.eufranio.pixelbuiltquests.quest.executor.QuestExecutorType;
import dev.eufranio.pixelbuiltquests.quest.executor.QuestExecutorTypeRegistryModule;
import dev.eufranio.pixelbuiltquests.registry.PBQRegistry;
import dev.eufranio.pixelbuiltquests.reward.RewardRegistryModule;
import dev.eufranio.pixelbuiltquests.reward.RewardType;
import dev.eufranio.pixelbuiltquests.storage.StorageManager;
import dev.eufranio.pixelbuiltquests.task.TaskRegistryModule;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerManager;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerTypeRegistryModule;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class PixelBuiltQuests {

    public static final String MOD_ID = "pixelbuiltquests";

    public static Logger LOGGER;
    private static File configDirectory;
    private static final PBQRegistry registry = new PBQRegistry();

    public static PixelBuiltQuests instance = null;
    private static List<UUID> runningQuests = Lists.newArrayList();

    final TriggerManager triggerManager = new TriggerManager();
    final StorageManager storage = new StorageManager();
    PlaceholderHandler placeholderHandler;
    MinecraftServer server;

    // LISTENERS

    public PixelBuiltQuests() {
        instance = this;
        LOGGER = LoggerFactory.getLogger(MOD_ID);
        configDirectory = Platform.getConfigFolder().resolve(MOD_ID).toFile();
        CommandRegistrationEvent.EVENT.register((dispatcher, registry1, selection) -> {
            dispatcher.register(PBQCommand.command());
        });
        LifecycleEvent.SERVER_BEFORE_START.register(state -> {
            server = state;
            setup();
        });
        LifecycleEvent.SERVER_STARTED.register(state -> {
            started();
        });
    }

    private void setup() {
        ConfigManager.loadConfig();
        registerListeners();
    }

    private void registerListeners() {
        PBQEvents.REGISTER_MODULES.register(registry -> {
            registry.register(TriggerType.class, new TriggerTypeRegistryModule());
            registry.register(QuestExecutorType.class, new QuestExecutorTypeRegistryModule());
            registry.register(TaskType.class, new TaskRegistryModule());
            registry.register(RewardType.class, new RewardRegistryModule());
            registry.register(Quest.class, new QuestRegistryModule());
            registry.register(QuestLine.class, new QuestLineRegistryModule());
        });

        PBQEvents.REGISTER_DEFAULTS.register((type, registry, module) -> {
            if (List.of(TaskType.class, RewardType.class, QuestExecutorType.class, TriggerType.class).contains(type)) {
                module.registerDefaults();
            }
        });

        PBQEvents.REGISTER_TYPES.register((type, registry, module) -> {
            if (type.equals(Quest.class)) {
                ConfigManager.registerQuests(registry);
            } else if (type.equals(QuestLine.class)) {
                ConfigManager.registerQuestLines(registry);
            } else if (type.equals(TaskType.class)) {
                ConfigManager.saveRegisteredTasks();
            } else if (type.equals(RewardType.class)) {
                ConfigManager.saveRegisteredRewards();
            }
        });

        PBQEvents.POST_REGISTER.register((type, registry, module) -> {
            if ((type.equals(Quest.class) || type.equals(QuestLine.class)) && module.getAll().isEmpty()) {
                module.registerDefaults();
            }
        });
    }

    private void started() {
        LOGGER.info("PixelBuilt - Quests is starting!");

        registry().loadRegistry();

        this.storage.init();
        this.triggerManager.init();

        if (Platform.getOptionalMod("placeholderapi").isPresent() && placeholderHandler == null) {
            LOGGER.info("Detected PlaceholderAPI, registering placeholders");
            placeholderHandler = new PlaceholderHandler();
            //placeholderHandler.init();
        }

        PBQEvents.RELOAD.register(() -> {
            LOGGER.info("Reloading PixelBuiltQuests");

            PBQEvents.REGISTER_MODULES.clearListeners();
            PBQEvents.REGISTER_DEFAULTS.clearListeners();
            PBQEvents.REGISTER_TYPES.clearListeners();
            PBQEvents.POST_REGISTER.clearListeners();
            PBQEvents.RELOAD.clearListeners();

            registry.reset();
            setup();
            started();

            LOGGER.info("Reload complete.");
        });
    }

    public static StorageManager storage() {
        return instance.storage;
    }

    public static PixelBuiltQuests instance() {
        return instance;
    }

    public Logger logger() {
        return LOGGER;
    }

    public File configDir() {
        return configDirectory;
    }

    public static PBQRegistry registry() {
        return registry;
    }

    public static TriggerManager triggerManager() {
        return instance.triggerManager;
    }

    public static @NotNull MinecraftServer server() {
        return instance.server;
    }

    public static boolean isRunningQuest(UUID player) {
        return runningQuests.contains(player);
    }

    public static void setRunningQuest(UUID player, boolean running) {
        if (running) {
            runningQuests.add(player);
        } else {
            runningQuests.remove(player);
        }
    }

}
