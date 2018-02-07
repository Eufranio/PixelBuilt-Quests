package online.pixelbuilt.pbquests.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import org.spongepowered.api.scheduler.Task;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frani on 18/12/2017.
 */
public class ConfigManager<T> {

    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode config;
    private T node;
    private PixelBuiltQuests plugin;
    private T configInstance;
    private String file;
    private Task task;

    public ConfigManager(T configInstance, String file, boolean autoSave) {
        this.configInstance = configInstance;
        this.file = file;
        this.plugin = PixelBuiltQuests.instance;
        if (!plugin.configDir.exists()) {
            plugin.configDir.mkdirs();
        }
        if (autoSave) {
            this.task = Task.builder()
                    .interval(60, TimeUnit.SECONDS)
                    .delayTicks(60)
                    .execute(this::reload)
                    .async()
                    .submit(this.plugin);
        }
    }

    @SuppressWarnings("unchecked")
    public ConfigManager<T> load() {
        try {
            File c = new File(plugin.configDir, file);
            if (!c.exists()) c.createNewFile();
            this.loader = HoconConfigurationLoader.builder().setFile(c).build();
            config = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(plugin.factory).setShouldCopyDefaults(true));
            node = config.getValue(TypeToken.of((Class<T>) configInstance.getClass()), configInstance);
            loader.save(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public T getConfig() {
        return this.node;
    }

    @SuppressWarnings("unchecked")
    public void reload() {
        try {
            config.setValue(TypeToken.of((Class<T>) node.getClass()), node);
            loader.save(config);
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelTask() {
        this.task.cancel();
        this.reload();
    }

}
