package online.pixelbuilt.pbquests.config;

import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class Quest {

    @Setting
    private String item = "minecraft:stone";

    private ItemType itemType;

    @Setting(comment = "1 = min, 2 = exact, 3 = max")
    public int progressCheck = 3;

    @Setting
    private int progressRequired = 0;

    @Setting
    private int progressAfter = 0;

    @Setting
    private int timeBetweenMessages = 1;

    @Setting
    public String progressRequiredMessage = "none";

    @Setting
    public int questId = 0;

    @Setting
    private String questLine = "default";

    @Setting
    private String teleportTo = "0,0,0,world";

    @Setting
    private boolean teleportEnabled = true;

    @Setting
    private List<String> messages = new ArrayList<>();

    @Setting
    private List<String> commands = new ArrayList<>();

    @Setting
    private Map<String, String> toExecute = Maps.newHashMap();

    @Setting
    private boolean denyMovement = true;

    @Setting
    private int cost = 0;

    private Location<World> teleport;

    @Setting
    private boolean enforcePermission = false;

    @Setting
    public boolean oneTime = false;

    @Setting
    private String oneTimeFinishMessage = "&aYou just completed this one time quest!";

    public void run(Player p) {
        UUID player = p.getUniqueId();
        this.itemType = Sponge.getRegistry().getType(ItemType.class, item).orElse(null);

        Text error = this.hasAllRequeriments(p);
        if (error != null) {
            if (!error.toPlain().isEmpty()) {
                p.sendMessage(Text.of(Util.toText(PixelBuiltQuests.getConfig().messages.prefix), TextColors.RESET).concat(error));
            }
            return;
        }

        if (!PixelBuiltQuests.runningQuests.contains(player)) {
            PixelBuiltQuests.runningQuests.add(player);
        }

        // Add player to the list of busy players
        if (denyMovement) {
            if (PixelBuiltQuests.playersBusy.contains(player)) return;
            PixelBuiltQuests.playersBusy.add(player);
        }

        if (timeBetweenMessages == 0) {
            for (String s : this.messages) {
                p.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(s.replace("%player%", p.getName())));
            }
            this.continueTask(p.getUniqueId());
        } else {
            int count = 0;
            for (String msg : messages) {
                count += timeBetweenMessages;
                int fcount = count;
                Task.builder()
                        .delay(count, TimeUnit.SECONDS)
                        .execute(() -> {
                            Player pl = this.getPlayer(player);
                            if (pl != null) {
                                pl.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(msg.replace("%player%", pl.getName())));
                            }
                            if (fcount == messages.size()) {
                                Task.builder().delay(timeBetweenMessages, TimeUnit.SECONDS).execute(() -> this.continueTask(player)).submit(PixelBuiltQuests.instance);
                            }
                        })
                        .submit(PixelBuiltQuests.instance);
            }
        }
    }


    public Text hasAllRequeriments(Player p) {
        if (this.enforcePermission) {
            if (!p.hasPermission("pbq.quest." + this.questLine + "." + this.questId)) {
                return Util.toText(PixelBuiltQuests.getConfig().messages.noPerm);
            }
        }

        if (this.oneTime && PixelBuiltQuests.getStorage().hasRan(p.getUniqueId(), this)) {
            return Util.toText(PixelBuiltQuests.getConfig().messages.hasRan);
        }

        int playerProgress = PixelBuiltQuests.getStorage().getProgress(p.getUniqueId(), this.getLine());
        switch (this.progressCheck) {
            case 1:
                if (playerProgress < this.progressRequired) {
                    return Util.toText(PixelBuiltQuests.getConfig().messages.noProgressMin
                        .replace("%progress%", ""+this.progressRequired)
                    );
                }
                break;
            case 2:
                if (playerProgress != this.progressRequired) {
                    return Util.toText(PixelBuiltQuests.getConfig().messages.noProgressExact
                        .replace("%progress%", ""+this.progressRequired)
                    );
                }
                break;
            case 3:
                if (playerProgress > this.progressRequired) {
                    return Util.toText(PixelBuiltQuests.getConfig().messages.noProgressMax
                        .replace("%progress%", ""+this.progressRequired)
                    );
                }
                break;
        }

        if (this.cost > 0) {
            EconomyService service = Sponge.getServiceManager().provide(EconomyService.class).orElse(null);
            if (service == null) {
                PixelBuiltQuests.logger.error("PBQ needs an economy plugin if quest prices are enabled!");
                return Text.of(TextColors.RED, " An error ocurred while checking the requeriments of this quest, contact an staff!");
            }
            UniqueAccount account = service.getOrCreateAccount(p.getUniqueId()).orElse(null);
            BigDecimal cost = BigDecimal.valueOf(this.cost);
            TransactionResult result = account.withdraw(service.getDefaultCurrency(), cost, Sponge.getCauseStackManager().getCurrentCause());
            if (result.getResult() != ResultType.SUCCESS) {
                return Util.toText(PixelBuiltQuests.getConfig().messages.noMoney
                        .replace("%money%", ""+this.cost)
                );
            }
        }

        if (this.itemType != null) {
            if (!p.getInventory().query(this.itemType).peek(1).isPresent()) {
                return Util.toText(PixelBuiltQuests.getConfig().messages.noItem
                        .replace("%item%", this.item)
                );

            } else {
                p.getInventory().query(this.itemType).poll(1);
            }
        }

        return null;
    }

    public void continueTask(UUID player) {
        // Increase the player's progress
        if (this.progressAfter > 0) {
            PixelBuiltQuests.getStorage().setProgress(player, this.getLine(), progressAfter);
        }

        for (Map.Entry<String, String> entry : this.toExecute.entrySet()) {
            if (entry.getValue().equalsIgnoreCase("cmd")) {
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), entry.getKey().replace("%player%", getPlayer(player).getName()));
            } else if (entry.getValue().equalsIgnoreCase("msg")) {
                this.getPlayer(player).sendMessage(TextSerializers.FORMATTING_CODE.deserialize(entry.getKey().replace("%player%", this.getPlayer(player).getName())));
            }
        }

        // Execute the console commands
        for (String command : commands) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command.replace("%player%", getPlayer(player).getName()));
        }

        if (denyMovement) {
            if (PixelBuiltQuests.playersBusy.contains(player)) {
                PixelBuiltQuests.playersBusy.remove(player);
            }
        }

        Player p1 = this.getPlayer(player);
        if (this.teleportEnabled) {
            World world = Sponge.getServer().getWorld(teleportTo.split(",")[3]).orElse(null);
            if (world == null) {
                throw new RuntimeException("The world specified in the quest " + this.questId + " is invalid!");
            } else {
                String[] loc = this.teleportTo.split(",");
                this.teleport = new Location<>(world, Integer.valueOf(loc[0]), Integer.valueOf(loc[1]), Integer.valueOf(loc[2]));
            }

            // Teleport the player
            if (p1 != null) {
                p1.setLocation(this.teleport);
            }
        }

        PixelBuiltQuests.runningQuests.remove(player);

        if (p1 != null) {
            String finish = PixelBuiltQuests.getConfig().messages.finish;
            if (!finish.isEmpty()) {
                p1.sendMessage(Util.toText(finish.replace("%quest%", this.questLine)));
            }
        }

        PixelBuiltQuests.getStorage().run(player, this);
        if (this.oneTime && !this.oneTimeFinishMessage.isEmpty()) {
            this.getPlayer(player).sendMessage(Util.toText(this.oneTimeFinishMessage));
        }
    }

    public Player getPlayer(UUID uuid) {
        return Sponge.getServer().getPlayer(uuid).orElse(null);
    }

    public QuestLine getLine() {
        return PixelBuiltQuests.getConfig()
                .questLines
                .stream()
                .filter(q -> q.quests.contains(this))
                .findFirst()
                .orElse(null);
    }

    public int getId() {
        return this.questId;
    }

    public static Quest of(QuestLine line, int id) {
        return of(line.getName(), id);
    }

    public static Quest of(String line, int id) {
        return PixelBuiltQuests.getConfig().getQuest(line, id);
    }

}
