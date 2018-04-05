package online.pixelbuilt.pbquests;

import online.pixelbuilt.pbquests.config.Quest;
import online.pixelbuilt.pbquests.config.Trigger;
import online.pixelbuilt.pbquests.utils.ChatUtils;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Frani on 06/09/2017.
 */
public class Listeners {

    @Listener
    public void onMove(MoveEntityEvent event) {
        if (PixelBuiltQuests.getDatabase().getQuestFromNPC(event.getTargetEntity()) != null) {
            event.setCancelled(true);
        }
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player)event.getTargetEntity();
            if (PixelBuiltQuests.playersBusy.contains(player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }
            if (PixelBuiltQuests.runningQuests.containsKey(player.getUniqueId())) return;

            Location<World> from = event.getFromTransform().getLocation();
            Location<World> to = event.getToTransform().getLocation();
            if (from.getBlockX() == to.getBlockX() &&
                from.getBlockY() == to.getBlockY() &&
                from.getBlockZ() == to.getBlockZ()) return;

            Location<World> location = new Location<World>(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() - 1, player.getLocation().getZ());
            Trigger trigger = PixelBuiltQuests.getTriggers().at(location);
            if (trigger != null) {
                if (player.hasPermission("pbq.run")) {
                    Quest quest = trigger.getQuest();
                    if (quest != null) {
                        quest.run(player);
                    } else {
                        if (player.hasPermission("pbq.admin")) {
                            player.sendMessage(Util.toText(PixelBuiltQuests.getConfig().messages.noQuest));
                        }
                    }
                }
            }

        }
    }

    @Listener
    public void onInteractEntityPrimary(InteractEntityEvent.Primary event, @First Player p) {
        if (event.getHandType() == HandTypes.MAIN_HAND) {
            p.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
                if (item.getItem().getId().equalsIgnoreCase(PixelBuiltQuests.getConfig().questSettingsItem) && p.get(Keys.IS_SNEAKING).orElse(false)) {
                    if (!p.hasPermission("pbq.admin")) return;
                    event.setCancelled(true);
                    ChatUtils.waitForResponse(p, "&a Type an action for this entity &7[&9newquest&7 / &9rename &7/ &9delete&7]", (player, action) -> {
                        if (action.equalsIgnoreCase("newquest")) {
                            ChatUtils.waitForResponse(p, "&a Type the number of the Quest (e.g 1) that will be assigned to this NPC!", (player1, response) -> {
                                ChatUtils.waitForResponse(p, "&a Type the Quest line (e.g default) that will be assigned to this NPC!", (player2, response2) -> {
                                    int questNumber = Integer.valueOf(response);
                                    if (response2 != null && !response2.isEmpty()) {
                                        PixelBuiltQuests.getDatabase().addNPC(event.getTargetEntity(), response2, questNumber);
                                        p.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(" Sucessfully added this NPC to the list of Quest NPCs!"));
                                    }
                                });
                            });
                        } else if (action.equalsIgnoreCase("rename")) {
                            ChatUtils.waitForResponse(p, "&aType the name that should be assigned to this NPC", (player3, response) -> {
                                if (!response.isEmpty()) {
                                    event.getTargetEntity().offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(response));
                                }
                            });
                        } else if (action.equalsIgnoreCase("delete")) {
                            PixelBuiltQuests.getDatabase().removeNPC(event.getTargetEntity());
                            event.getTargetEntity().remove();
                        }
                    });
                }
            });
        }
    }

    @Listener
    public void onInteractEntitySecondary(InteractEntityEvent.Secondary event, @Root Player p) {
        Entity npc = event.getTargetEntity();
        if (PixelBuiltQuests.getDatabase().getQuestFromNPC(npc) != null && p.hasPermission("pbq.run")) {
            event.setCancelled(true);
            Quest quest = PixelBuiltQuests.getConfig().getQuestFor(PixelBuiltQuests.getDatabase().getQuestFromNPC(npc));
            if (quest != null) {
                quest.run(p);
            } else {
                if (p.hasPermission("pbq.admin"))
                    p.sendMessage(Util.toText(PixelBuiltQuests.getConfig().messages.noQuest));
            }
        }
    }

    @Listener
    public void onInteractBlockSecondary(InteractBlockEvent.Secondary event, @First Player p) {
        if (event.getHandType() == HandTypes.MAIN_HAND) {
            p.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
                if (item.getItem().getId().equalsIgnoreCase(PixelBuiltQuests.getConfig().questSettingsItem) && p.get(Keys.IS_SNEAKING).orElse(false)) {
                    if (!p.hasPermission("pbq.admin")) return;
                    event.setCancelled(true);
                    ChatUtils.waitForResponse(p, "&a Type an action for this block &7[&9newquest &7/ &9delete&7]", (player, action) -> {
                        if (action.equalsIgnoreCase("newquest")) {
                            ChatUtils.waitForResponse(p, "&a Type the number of the Quest (e.g 1) that will be assigned to this block!", (player1, questId) -> {
                                ChatUtils.waitForResponse(p, "&a Type the Quest line (e.g default) that will be assigned to this block!", (player2, questLine) -> {
                                    if (questLine != null && !questLine.isEmpty()) {
                                        PixelBuiltQuests.getTriggers().add(event.getTargetBlock().getLocation().get(), questLine, Integer.parseInt(questId));
                                        p.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&a Sucessfully added this block to the list of triggers!"));
                                    }
                                });
                            });
                        } else if (action.equalsIgnoreCase("delete")) {
                            event.getTargetBlock().getLocation().ifPresent(PixelBuiltQuests.getTriggers()::remove);
                            p.sendMessage(Text.of(TextColors.GREEN, " Successfully removed the Trigger from here!"));
                        }
                    });
                }
            });
        }
    }
}
