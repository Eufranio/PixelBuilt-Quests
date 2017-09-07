package online.pixelbuilt.pbquests;

import online.pixelbuilt.pbquests.utils.ChatUtils;
import online.pixelbuilt.pbquests.utils.Quest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Frani on 06/09/2017.
 */
public class Listeners {

    @Listener
    public void onMove(MoveEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {

            Player player = (Player)event.getTargetEntity();

            if (PixelBuiltQuests.playersBusy.contains(player)) {
                event.setCancelled(true);
                return;
            }

            if (PixelBuiltQuests.runningQuests.containsKey(player)) return;

            Location<World> location = new Location<World>(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() - 1, player.getLocation().getZ());
            if (PixelBuiltQuests.config.blocks.contains(location.getBlockType())) {
                if (PixelBuiltQuests.config.hasTrigger(location) && player.hasPermission("pbq.run")) {
                    int id = PixelBuiltQuests.config.getQuestId(location);
                    String questLine = PixelBuiltQuests.config.getQuestLine(location);
                    Quest quest = Quest.builder(player, questLine, id);
                    if (quest != null) {
                        quest.run();
                    } else {
                        if (player.hasPermission("pbq.admin")) player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&c There is no Quest with this quest line/id!"));
                    }
                }
            }

        }
    }

    @Listener
    public void onInteractEntitySecondary(InteractEntityEvent.Secondary event, @First Player p) {
        if (event.getHandType() == HandTypes.MAIN_HAND) {
            p.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
                if (item.getItem() == ItemTypes.ARROW && p.get(Keys.IS_SNEAKING).orElse(false)) {
                    if (!p.hasPermission("pbq.admin")) return;
                    event.setCancelled(true);
                    ChatUtils.waitForResponse(p, "&a Type an action for this entity &7[&9newquest&7 / &9rename &7/ &9delete&7]", (player, action) -> {
                        if (action.equalsIgnoreCase("newquest")) {
                            ChatUtils.waitForResponse(p, "&a Type the number of the Quest (e.g 1) that will be assigned to this NPC!", (player1, response) -> {
                                ChatUtils.waitForResponse(p, "&a Type the Quest line (e.g default) that will be assigned to this NPC!", (player2, response2) -> {
                                    int questNumber = Integer.valueOf(response);
                                    String questLine = response2;
                                    if (questLine != null && !questLine.isEmpty()) {
                                        PixelBuiltQuests.db.addNpc(questLine, questNumber, event.getTargetEntity());
                                        p.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(" Sucessfully added this NPC to the list of Quest NPCs!"));
                                        return;
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
                            if (PixelBuiltQuests.db.getQuestIdFromNPC(event.getTargetEntity()) >= 0) {
                                PixelBuiltQuests.db.removeNpc(event.getTargetEntity());
                            }
                            event.getTargetEntity().remove();
                        }
                    });
                }
            });

            Entity npc = event.getTargetEntity();
            if (PixelBuiltQuests.db.getQuestIdFromNPC(npc) >= 0 && p.hasPermission("pbq.run")) {
                event.setCancelled(true);
                Quest quest = Quest.builder(p, PixelBuiltQuests.db.getQuestLineFromNPC(npc), PixelBuiltQuests.db.getQuestIdFromNPC(npc));
                if (quest != null) {
                    quest.run();
                } else {
                    if (p.hasPermission("pbq.admin")) p.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&c There is no Quest with this quest line/id!"));
                }
            }

        }
    }

    @Listener
    public void onInteractBlockSecondary(InteractBlockEvent.Secondary event, @First Player p) {
        if (event.getHandType() == HandTypes.MAIN_HAND) {
            p.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
                if (item.getItem() == ItemTypes.ARROW && p.get(Keys.IS_SNEAKING).orElse(false)) {
                    if (!p.hasPermission("pbq.admin")) return;
                    event.setCancelled(true);
                    ChatUtils.waitForResponse(p, "&a Type an action for this block &7[&9newquest &7/ &9delete&7]", (player, action) -> {
                        if (action.equalsIgnoreCase("newquest")) {

                            ChatUtils.waitForResponse(p, "&a Type the number of the Quest (e.g 1) that will be assigned to this block!", (player1, questId) -> {
                                ChatUtils.waitForResponse(p, "&a Type the Quest line (e.g default) that will be assigned to this block!", (player2, questLine) -> {
                                    if (questLine != null && !questLine.isEmpty()) {
                                        if (!PixelBuiltQuests.config.blocks.contains(event.getTargetBlock().getState().getType())) {
                                            p.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&c This block isn't in the config list of trigger blocks! Add it there first!"));
                                            return;
                                        }
                                        PixelBuiltQuests.config.addTrigger(event.getTargetBlock().getLocation().get(), questLine, Integer.parseInt(questId));
                                        p.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(" Sucessfully added this block to the list of triggers!"));
                                        return;
                                    }
                                });
                            });
                        } else if (action.equalsIgnoreCase("delete")) {
                            // delete here
                        }
                    });
                }
            });
        }
    }
}
