package dev.eufranio.pixelbuiltquests.trigger;

import com.j256.ormlite.dao.Dao;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.storage.sql.Trigger;
import io.github.eufranio.storage.Persistable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TriggerManager {

    private Persistable<Trigger, Integer> triggers;
    final List<Trigger> cachedTriggers = new ArrayList<>();

    public void init() {
        cachedTriggers.clear();
        try {
            String url = ConfigManager.getConfig().database.url;
            triggers = Persistable.create(Trigger.class, url);
            cachedTriggers.addAll(triggers.objDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Trigger trigger) {
        this.triggers.delete(trigger);
        this.cachedTriggers.remove(trigger);
    }

    public void saveTrigger(Trigger trigger) {
        try {
            trigger.create();
            cachedTriggers.add(trigger);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Trigger> getTriggers() {
        return cachedTriggers;
    }

    public Persistable<Trigger, Integer> triggerDao() {
        return triggers;
    }

}
