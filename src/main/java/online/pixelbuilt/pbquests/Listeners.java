package online.pixelbuilt.pbquests;

import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.config.Trigger;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.impl.VisitTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Frani on 06/09/2017.
 */
public class Listeners {

    @Listener
    public void onMove(MoveEntityEvent event, @Getter("getTargetEntity") Player player) {
        if (PixelBuiltQuests.playersBusy.contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (PixelBuiltQuests.runningQuests.contains(player.getUniqueId())) return;

        Location<World> from = event.getFromTransform().getLocation();
        Location<World> to = event.getToTransform().getLocation();
        if (from.getBlockPosition().equals(to.getBlockPosition())) return;

        Location<World> location = to.sub(0, 1, 0);
        Trigger trigger = PixelBuiltQuests.getStorage().getTriggerAt(location);
        if (trigger != null && trigger.onWalk && player.hasPermission("pbq.run")) {
            Tuple<Quest, QuestLine> quest = trigger.getQuest();
            if (quest != null) {
                quest.getFirst().getExecutor().execute(quest.getFirst(), quest.getSecond(), quest.getFirst().getId(), player);
            } else {
                if (player.hasPermission("pbq.admin")) {
                    player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noQuest));
                }
            }
        }

        VisitTask.locations.keys().stream()
                .filter(l -> {
                    Location<World> loc = l.getLocation();
                    if (loc.equals(to)) return true;
                    if (loc.getExtent().equals(to.getExtent()) && loc.getPosition().distanceSquared(to.getPosition()) <= l.visitRadius) {
                        return true;
                    }
                    return false;
                })
                .findFirst()
                .ifPresent(entry -> VisitTask.locations.put(entry, UUID.randomUUID()));
    }

    @Listener
    public void onInteractEntitySecondary(InteractEntityEvent.Secondary.MainHand event, @Root Player p) {
        Entity npc = event.getTargetEntity();
        Tuple<Quest, QuestLine> info = PixelBuiltQuests.getStorage().getQuest(npc);
        if (info != null && p.hasPermission("pbq.run")) {
            event.setCancelled(true);
            info.getFirst().getExecutor().execute(info.getFirst(), info.getSecond(), info.getFirst().getId(), p);
        }
    }
}
