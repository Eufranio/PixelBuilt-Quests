package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import online.pixelbuilt.pbquests.task.TriggeredTask;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Frani on 28/01/2019.
 */
@ConfigSerializable
public class VisitTask implements TriggeredTask<MoveEntityEvent> {

    @Setting
    public int id = 5;

    @Setting
    public String visitLocation = "0,0,0,world";

    @Setting
    public int visitRadius = 5;

    @Override
    public TaskType getType() {
        return TaskTypes.VISIT;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getTotal() {
        return 1;
    }

    @Override
    public Text toText() {
        String[] arr = this.visitLocation.split(",");
        return Text.of(TextColors.YELLOW, "Visit (", Text.of(TextColors.AQUA, "x=", arr[0], ", y=", arr[1], ", z=", arr[2], ", ", arr[3]), ")");
    }

    @Override
    public Class<MoveEntityEvent> getEventClass() {
        return MoveEntityEvent.class;
    }

    @Override
    public void handle(QuestLine line, Quest quest, MoveEntityEvent event) {
        if (event.getCause().root() instanceof Player) {
            Player player = (Player) event.getCause().root();
            Location<World> loc = this.getLocation();
            if (!player.getWorld().getUniqueId().equals(loc.getExtent().getUniqueId()))
                return;

            Location<World> from = event.getFromTransform().getLocation();
            Location<World> to = event.getToTransform().getLocation();
            if (from.getBlockPosition().equals(to.getBlockPosition()))
                return;

            if (to.getBlockPosition().distance(loc.getBlockPosition()) <= visitRadius) {
                PlayerData data = PixelBuiltQuests.getStorage().getData(player.getUniqueId());
                if (!this.isCompleted(data, line, quest)) {
                    data.getStatus(this, line, quest)
                            .ifPresent(status -> this.increase(data, status, 1));
                }
            }
        }
    }

    Location<World> loc;

    public Location<World> getLocation() {
        if (loc == null) {
            String[] string = this.visitLocation.split(",");
            World world = Sponge.getServer().getWorld(string[3]).orElse(null);
            if (world == null) return null;
            loc = new Location<>(world, Integer.parseInt(string[0]), Integer.parseInt(string[1]), Integer.parseInt(string[2]));
        }
        return loc;
    }

}
