package online.pixelbuilt.pbquests.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.utils.ChatUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frani on 16/12/2017.
 */
@ConfigSerializable
public class Quest {

    @Setting
    public String item = "minecraft:stone";

    public ItemType itemType;

    @Setting
    public int progressRequired = 0;

    @Setting
    public int progressAfter = 0;

    @Setting
    public int timeBetweenMessages = 1;

    @Setting
    public String progressRequiredMessage = "none";

    @Setting
    public int questId = 0;

    @Setting
    public String questLine = "default";

    @Setting
    public String teleportTo = "0,0,0,world";

    @Setting
    public List<String> messages = new ArrayList<>();

    @Setting
    public List<String> commands = new ArrayList<>();

    @Setting
    public boolean denyMovement = true;

    @Setting
    public int cost = 0;

    public UUID player;

    public Location<World> teleport;

    @Setting
    public boolean enforcePermission = false;

    public void run(Player p) {
        this.player = p.getUniqueId();
        this.itemType = Sponge.getRegistry().getType(ItemType.class, item).orElse(null);

        Text error = this.hasAllRequeriments(p);
        if (error != null) {
            p.sendMessage(Text.of(TextColors.BLUE, TextStyles.BOLD, " Quests > ", TextColors.RESET).concat(error));
            return;
        }

        if (!PixelBuiltQuests.runningQuests.containsKey(player)) {
            PixelBuiltQuests.runningQuests.put(player, this);
        }

        // Add player to the list of busy players
        if (denyMovement) {
            if (PixelBuiltQuests.playersBusy.contains(player)) return;
            PixelBuiltQuests.playersBusy.add(player);
        }

        int count = 0;
        for (String msg : messages) {
            count += timeBetweenMessages;
            int fcount = count;
            Task.builder()
                    .delay(count, TimeUnit.SECONDS)
                    .execute(() -> {
                        getPlayer().sendMessage(TextSerializers.FORMATTING_CODE.deserialize(msg.replace("%player%", getPlayer().getName())));
                        if (fcount == messages.size()) {
                            Task.builder().delay(timeBetweenMessages, TimeUnit.SECONDS).execute(this::continueTask).submit(PixelBuiltQuests.instance);
                        }
                    })
                    .submit(PixelBuiltQuests.instance);
        }

        if (PixelBuiltQuests.runningQuests.containsKey(player)) {
            PixelBuiltQuests.runningQuests.remove(player);
        }

        if (denyMovement) {
            if (!PixelBuiltQuests.playersBusy.contains(player)) return;
            PixelBuiltQuests.playersBusy.remove(player);
        }
    }


    public Text hasAllRequeriments(Player p) {
        if (this.enforcePermission) {
            if (!p.hasPermission("pbq.quest." + this.questLine + this.questId)) {
                return Text.of(TextColors.RED, " You don't have permission to run this quest!");
            }
        }

        if (PixelBuiltQuests.getDatabase().getProgress(player, this.questLine) < this.progressRequired) {
            return Text.of(TextColors.RED, "You need to have at least level ",
                    TextColors.YELLOW, this.progressRequired,
                    TextColors.RED, " on the quests to run this one!");
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
                return Text.of(TextColors.RED, " You must have $",
                        TextColors.YELLOW, this.cost,
                        TextColors.RED, " to complete this quest!");
            }
        }

        if (this.itemType != null) {
            if (!getPlayer().getInventory().query(this.itemType).peek(1).isPresent()) {
                return Text.of(
                        TextColors.RED, " You need at least one ",
                        TextColors.YELLOW, this.item,
                        TextColors.RED, " to complete this quest!"
                );
            } else {
                getPlayer().getInventory().query(this.itemType).poll(1);
            }
        }

        return null;
    }

    public void continueTask() {

        // Increase the player's progress
        if (this.progressAfter > 0) {
            PixelBuiltQuests.getDatabase().setProgress(player, questLine, progressAfter);
        }

        // Execute the console commands
        for (String command : commands) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command.replace("%player%", getPlayer().getName()));
        }

        if (denyMovement) {
            if (PixelBuiltQuests.playersBusy.contains(player)) {
                PixelBuiltQuests.playersBusy.remove(player);
            }
        }

        World world = Sponge.getServer().getWorld(teleportTo.split(",")[3]).orElse(null);
        if (world == null) {
            PixelBuiltQuests.logger.error("This world is invalid!");
        } else {
            String[] loc = this.teleportTo.split(",");
            this.teleport = new Location<>(world, Integer.valueOf(loc[0]), Integer.valueOf(loc[1]), Integer.valueOf(loc[2]));
        }

        // Teleport the player
        getPlayer().setLocation(this.teleport);

        if (PixelBuiltQuests.runningQuests.containsKey(player)) {
            PixelBuiltQuests.runningQuests.remove(player);
        }
    }

    public Player getPlayer() {
        return Sponge.getServer().getPlayer(player).get();
    }

}
