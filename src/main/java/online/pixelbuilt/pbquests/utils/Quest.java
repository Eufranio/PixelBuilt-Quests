package online.pixelbuilt.pbquests.utils;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.persistence.QuestPersistenceService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frani on 05/09/2017.
 */
public class Quest {

    public ItemType item;
    public int progressRequired, progressAfter, timeBetweenMessages;
    public String progressRequiredMessage, questLine;
    public Location<World> teleportTo;
    public List<String> messages, commands;
    public Player player;
    public boolean denyMovement;

    private Quest(Player player, String questLine, int questID) {
        try {
            ConfigurationNode n = PixelBuiltQuests.config.getQuestNode(questLine, questID);
            this.questLine = questLine;
            this.denyMovement = n.getNode("denyMovement").getBoolean();
            if (Sponge.getRegistry().getType(ItemType.class, n.getNode("requiredItem").getString()).isPresent()) {
                this.item = Sponge.getRegistry().getType(ItemType.class, n.getNode("requiredItem").getString()).get();
            }

            this.progressRequired = n.getNode("progressRequired").getInt();
            this.progressRequiredMessage = n.getNode("progressRequiredMessage").getString();
            this.progressAfter = n.getNode("progressAfter").getInt();

            Optional<World> world = Sponge.getServer().getWorld(n.getNode("teleportTo", "world").getString());
            world.ifPresent(w ->
                    this.teleportTo = new Location<World>(w,
                            n.getNode("teleportTo", "x").getInt(),
                            n.getNode("teleportTo", "y").getInt(),
                            n.getNode("teleportTo", "z").getInt())
            );

            if (teleportTo == null) {
                PixelBuiltQuests.logger.error("The name " + n.getNode("teleportTo", "world").getString() + " isn't a valid world!");
                return;
            }

            this.timeBetweenMessages = n.getNode("timeBetweenMessages").getInt();
            this.messages = n.getNode("messages").getList(TypeToken.of(String.class));
            this.commands = n.getNode("runOnComplete").getList(TypeToken.of(String.class));
            this.player = player;
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public static Quest builder(Player p, String questLine, int questId) {
        if (PixelBuiltQuests.config.getQuestNode(questLine, questId).isVirtual()) {
            return null;
        }
        return new Quest(p, questLine, questId);
    }

    public void run() {
        if (!PixelBuiltQuests.runningQuests.containsKey(player.getUniqueId())) {
            PixelBuiltQuests.runningQuests.put(player.getUniqueId(), this);
        }

        // Add player to the list of busy players
        if (denyMovement) {
            if (PixelBuiltQuests.playersBusy.contains(player.getUniqueId())) return;
            PixelBuiltQuests.playersBusy.add(player.getUniqueId());
        }

        // Player has the required progress level
        QuestPersistenceService service = Sponge.getServiceManager().provide(QuestPersistenceService.class).get();


        service.getProgress(player, questLine, i -> {
            if (i == progressRequired) {
                int count = 0;
                if (item != null) {
                    if (!player.getInventory().queryAny(ItemStack.builder().itemType(item).build()).poll(1).isPresent()) {
                        player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&c You need a &e" + item.getName() + "&c to complete this Quest!"));
                        if (PixelBuiltQuests.runningQuests.containsKey(player.getUniqueId())) {
                            PixelBuiltQuests.runningQuests.remove(player.getUniqueId());
                        }
                        if (denyMovement) {
                            if (!PixelBuiltQuests.playersBusy.contains(player.getUniqueId())) return;
                            PixelBuiltQuests.playersBusy.remove(player.getUniqueId());
                        }
                        return;
                    }
                }

                // Send the Quest messages
                for (String msg : messages) {
                    count += timeBetweenMessages;
                    int fcount = count;
                    Task.builder()
                            .delay(count, TimeUnit.SECONDS)
                            .execute(() -> {
                                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(msg.replace("%player%", player.getName())));
                                if (fcount == messages.size()) {
                                    Task.builder().delay(timeBetweenMessages, TimeUnit.SECONDS).execute(() -> continueTask()).submit(PixelBuiltQuests.instance);
                                }
                            })
                            .submit(PixelBuiltQuests.instance);
                }
                return;

            } else if (i == -1) {
                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(progressRequiredMessage.replace("%player%", player.getName())));
            }
        });


        if (PixelBuiltQuests.runningQuests.containsKey(player.getUniqueId())) {
            PixelBuiltQuests.runningQuests.remove(player.getUniqueId());
        }

        if (denyMovement) {
            if (!PixelBuiltQuests.playersBusy.contains(player.getUniqueId()))
                return;
            PixelBuiltQuests.playersBusy.remove(player.getUniqueId());
        }
    }

    public void continueTask() {

        // Increase the player's progress
        QuestPersistenceService service = Sponge.getServiceManager().provide(QuestPersistenceService.class).get();
        service.setProgressLevel(player, questLine, progressAfter, () -> {

            // Execute the console commands
            for (String command : commands) {
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command.replace("%player%", player.getName()));
            }

            if (denyMovement) {
                if (PixelBuiltQuests.playersBusy.contains(player.getUniqueId())) {
                    PixelBuiltQuests.playersBusy.remove(player.getUniqueId());
                }
            }

            // Teleport the player
            player.setLocation(teleportTo);

            if (PixelBuiltQuests.runningQuests.containsKey(player.getUniqueId())) {
                PixelBuiltQuests.runningQuests.remove(player.getUniqueId());
            }
        });


    }
}
