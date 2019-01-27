package online.pixelbuilt.pbquests.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import online.pixelbuilt.pbquests.PixelBuiltQuests;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Frani on 03/11/2018.
 */
public class Config<T> {

    private HoconConfigurationLoader loader;
    private CommentedConfigurationNode node;
    private Class<T> clazz;
    private TypeToken<T> token;
    private T value;

    public Config(Class<T> clazz, String name, Path configDir) {
        this(clazz, name, configDir.toFile());
    }

    public Config(Class<T> clazz, String name, File configDir) {
        if (!configDir.exists()) configDir.mkdirs();

        File file = new File(configDir, name);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (Exception e) { e.printStackTrace(); }

        this.clazz = clazz;
        this.token = TypeToken.of(clazz);
        this.loader = HoconConfigurationLoader.builder()
                .setFile(file)
                .build();
        this.value = load(false);
    }

    private T load(boolean set) {
        try {
            this.node = this.loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            T value = set ? this.value : this.node.getNode("config").getValue(token, clazz.newInstance());
            this.node.getNode("config").setValue(token, value);
            this.loader.save(this.node);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reload() {
        this.value = this.load(false);
    }

    public T get() {
        return this.value;
    }

    public void save() {
        this.load(true);
    }

}
