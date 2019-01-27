package online.pixelbuilt.pbquests.storage;

import com.google.common.collect.Lists;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.config.Trigger;
import org.apache.commons.lang3.SerializationUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Created by Frani on 10/11/2018.
 */
public class SQLStorage implements StorageModule {

    private DataSource src;

    private List<Trigger> triggers = Lists.newArrayList();

    @Override
    public void init(PixelBuiltQuests instance) {
        try {
            SqlService service = Sponge.getServiceManager().provideUnchecked(SqlService.class);
            this.src = service.getDataSource(ConfigManager.getConfig().database.url);

            try (Connection c = this.src.getConnection()) {
                Statement s = c.createStatement();
                s.executeUpdate("CREATE TABLE IF NOT EXISTS triggers (" +
                        "x INT, " +
                        "y INT, " +
                        "z INT, " +
                        "world VARCHAR(36), " +
                        "line VARCHAR(200), " +
                        "id INT, " +
                        "walk INT" + // boolean
                        ");");

                s.executeUpdate("CREATE TABLE IF NOT EXISTS players (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "progress VARBINARY(65000), " +
                        "quests VARBINARY(65000)" +
                        ");");

                s.executeUpdate("CREATE TABLE IF NOT EXISTS npcs (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "line VARCHAR(200), " +
                        "id INT" +
                        ");");

                // populating triggers
                ResultSet set = s.executeQuery("SELECT * FROM triggers;");
                while (set.next()) {
                    this.triggers.add(new Trigger(
                            set.getInt("x"),
                            set.getInt("y"),
                            set.getInt("z"),
                            UUID.fromString(set.getString("world")),
                            set.getString("line"),
                            set.getInt("id"),
                            set.getBoolean("walk")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getProgress(UUID player, QuestLine line) {
        try (Connection c = this.src.getConnection()) {
            Statement s = c.createStatement();
            ResultSet set = s.executeQuery("SELECT progress FROM players WHERE uuid='" + player.toString() + "';");
            if (set.next()) {
                HashMap<String, Integer> progress = SerializationUtils.deserialize(set.getBytes("progress"));
                return progress.getOrDefault(line.getName(), 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void setProgress(UUID player, QuestLine line, int progress) {
        try (Connection c = this.src.getConnection()) {
            Statement s = c.createStatement();
            ResultSet set = s.executeQuery("SELECT progress FROM players WHERE uuid='" + player.toString() + "';");
            if (set.next()) {
                HashMap<String, Integer> map = SerializationUtils.deserialize(set.getBytes("progress"));
                map.put(line.getName(), progress);

                PreparedStatement s2 = c.prepareStatement("UPDATE players SET progress=? WHERE uuid=?;");
                s2.setBytes(1, SerializationUtils.serialize(map));
                s2.setString(2, player.toString());
                s2.executeUpdate();
            } else {
                HashMap<String, Integer> map = new HashMap<>();
                map.put(line.getName(), progress);

                PreparedStatement s2 = c.prepareStatement("INSERT INTO players VALUES(?, ?, ?)");
                s2.setString(1, player.toString());
                s2.setBytes(2, SerializationUtils.serialize(map));
                s2.setBytes(3, SerializationUtils.serialize(new ArrayList<String>()));
                s2.executeUpdate();
                s2.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Tuple<Quest, QuestLine> getQuest(Entity npc) {
        try (Connection c = this.src.getConnection()) {
            Statement s = c.createStatement();
            ResultSet set = s.executeQuery("SELECT line, id FROM npcs WHERE uuid='" + npc.getUniqueId().toString() + "';");
            if (set.next()) {
                String line = set.getString("line");
                int id = set.getInt("id");
                return new Tuple<>(ConfigManager.getQuest(id), ConfigManager.getLine(line));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addNPC(Entity npc, QuestLine line, int questId) {
        try (Connection c = this.src.getConnection()) {
            Statement s = c.createStatement();
            s.executeUpdate("INSERT INTO npcs VALUES ('" +
                    npc.getUniqueId().toString() + "', '" +
                    line.getName() + "', '" +
                    questId +
                    "');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeNPC(Entity npc) {
        try (Connection c = this.src.getConnection()) {
            Statement s = c.createStatement();
            s.executeUpdate("DELETE FROM npcs WHERE uuid='" + npc.getUniqueId() + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasRan(UUID player, QuestLine line, int questId) {
        try (Connection c = this.src.getConnection()) {
            Statement s = c.createStatement();
            ResultSet set = s.executeQuery("SELECT quests FROM players WHERE uuid='" + player + "';");
            if (set.next()) {
                ArrayList<String> list = SerializationUtils.deserialize(set.getBytes("quests"));
                return list.contains(line.getName().toLowerCase() + "," + questId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run(UUID player, QuestLine line, int questId) {
        try (Connection c = this.src.getConnection()) {
            Statement s = c.createStatement();
            ResultSet set = s.executeQuery("SELECT quests FROM players WHERE uuid='" + player + "';");
            if (set.next()) {
                ArrayList<String> list = SerializationUtils.deserialize(set.getBytes("quests"));
                list.add(line.getName() + "," + questId);
                try (PreparedStatement s2 = c.prepareStatement("UPDATE players SET quests=? WHERE uuid=?;")) {
                    s2.setBytes(1, SerializationUtils.serialize(list));
                    s2.setString(2, player.toString());
                    s2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Trigger getTriggerAt(Location<World> location) {
        return this.triggers.stream()
                .filter(t -> t.getLocation().getBlockPosition().equals(location.getBlockPosition()))
                .filter(t -> t.getLocation().getExtent().getUniqueId().equals(location.getExtent().getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addTrigger(Trigger trigger) {
        try (Connection c = this.src.getConnection()) {
            PreparedStatement s = c.prepareStatement("INSERT INTO triggers VALUES(?, ?, ?, ?, ?, ?, ?);");
            s.setInt(1, trigger.x);
            s.setInt(2, trigger.y);
            s.setInt(3, trigger.z);
            s.setString(4, trigger.worldUUID.toString());
            s.setString(5, trigger.getQuest().getSecond().getName());
            s.setInt(6, trigger.getQuest().getFirst().getId());
            s.setBoolean(7, trigger.onWalk);
            s.executeUpdate();

            this.triggers.add(trigger);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void removeTrigger(Trigger trigger) {
        try (Connection c = this.src.getConnection()) {
            PreparedStatement s = c.prepareStatement("DELETE FROM triggers WHERE x=? AND y=? AND z=? AND world=?;");
            s.setInt(1, trigger.x);
            s.setInt(2, trigger.y);
            s.setInt(3, trigger.z);
            s.setString(4, trigger.worldUUID.toString());
            s.executeUpdate();

            this.triggers.remove(trigger);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Trigger> getTriggers() {
        return this.triggers;
    }

    @Override
    public List<String> getQuestsRan(UUID player) {
        try (Connection c = this.src.getConnection()) {
            ResultSet set = c.createStatement().executeQuery("SELECT quests FROM players WHERE uuid='" + player + "';");
            if (set.next()) {
                return SerializationUtils.deserialize(set.getBytes("quests"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void resetQuest(UUID player, QuestLine line, int questId) {
        try (Connection c = this.src.getConnection()) {
            Statement s = c.createStatement();
            ResultSet set = s.executeQuery("SELECT quests FROM players WHERE uuid='" + player + "';");
            if (set.next()) {
                ArrayList<String> list = SerializationUtils.deserialize(set.getBytes("quests"));
                list.remove(line.getName() + "," + questId);

                PreparedStatement s2 = c.prepareStatement("UPDATE players SET quests=? WHERE uuid=?;");
                s2.setBytes(1, SerializationUtils.serialize(list));
                s2.setString(2, player.toString());
                s2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        this.src = null;
        this.triggers.clear();
    }

}
