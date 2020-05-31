package online.pixelbuilt.pbquests.listeners;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.Trigger;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Frani on 06/09/2017.
 */
public class Listeners {

    PixelBuiltQuests plugin;
    public Listeners(PixelBuiltQuests plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onMove(MoveEntityEvent event, @Getter("getTargetEntity") Player player) {
        if (plugin.playersBusy.contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (plugin.runningQuests.contains(player.getUniqueId())) return;

        Location<World> from = event.getFromTransform().getLocation();
        Location<World> to = event.getToTransform().getLocation();
        if (from.getBlockPosition().equals(to.getBlockPosition())) return;

        Location<World> location = to.sub(0, 1, 0);
        Trigger trigger = plugin.getStorage().getTriggerAt(location);
        if (trigger != null && trigger.type == Trigger.Type.WALK && player.hasPermission("pbq.run")) {
            Quest quest = trigger.getQuest();
            if (quest != null) {
                quest.getExecutor().execute(quest, trigger.getQuestLine(), player);
            } else {
                if (player.hasPermission("pbq.admin")) {
                    player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noQuest));
                }
            }
        }
    }

    @Listener(order = Order.EARLY, beforeModifications = true)
    public void onInteractEntitySecondary(InteractEntityEvent.Secondary.MainHand event, @Root Player p) {
        if (plugin.runningQuests.contains(p.getUniqueId()))
            return;

        Entity npc = event.getTargetEntity();
        Trigger trigger = plugin.getStorage().getTrigger(npc);
        if (trigger != null && p.hasPermission("pbq.run")) {
            event.setCancelled(trigger.cancelOriginalAction);
            trigger.getQuest().getExecutor().execute(trigger.getQuest(), trigger.getQuestLine(), p);
        }
    }

    @Listener(order = Order.EARLY, beforeModifications = true)
    @Include({InteractBlockEvent.Primary.MainHand.class, InteractBlockEvent.Secondary.MainHand.class})
    public void onInteractBlock(InteractBlockEvent event, @Root Player player) {
        if (plugin.runningQuests.contains(player.getUniqueId()))
            return;

        Trigger trigger = plugin.getStorage().getTriggerAt(event.getTargetBlock().getLocation().get());
        if (trigger != null &&
                (trigger.type == Trigger.Type.CLICK ||
                (trigger.type == Trigger.Type.RIGHT_CLICK && event instanceof InteractBlockEvent.Secondary) ||
                (trigger.type == Trigger.Type.LEFT_CLICK && event instanceof InteractBlockEvent.Primary))) {

            event.setCancelled(trigger.cancelOriginalAction);
            trigger.getQuest().getExecutor().execute(trigger.getQuest(), trigger.getQuestLine(), player);
        }
    }
}
