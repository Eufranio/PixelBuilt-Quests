package online.pixelbuilt.pbquests.utils;

import io.github.eufranio.pbqmessages.PBQMessages;
import javafx.util.Pair;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.DatabaseCategory;
import online.pixelbuilt.pbquests.config.Quest;
import online.pixelbuilt.pbquests.config.QuestLine;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Frani on 06/09/2017.
 */
public class Command {

    public static void registerCommand() {

        CommandSpec resetOneTime = CommandSpec.builder()
                .description(Text.of("Resets one time quests of a specific quest line"))
                .permission("pbquests.reset")
                .arguments(
                        GenericArguments.string(Text.of("quest line")),
                        GenericArguments.optional(
                                GenericArguments.user(Text.of("player"))
                        )
                )
                .executor((sender, context) -> {
                    String line = context.<String>getOne("quest line").get();
                    User user = context.<User>getOne("player").orElse(null);
                    if (user == null) {
                        if (sender instanceof Player) {
                            user = (User) sender;
                        } else {
                            throw new CommandException(Text.of("Specify a player if running this command from console!"));
                        }
                    }
                    List<Integer> quests = PixelBuiltQuests.getStorage().getQuestsRan(user.getUniqueId())
                            .stream()
                            .filter(s -> s.split(",")[0].equalsIgnoreCase(line))
                            .map(s -> Integer.parseInt(s.split(",")[1]))
                            .filter(i -> {
                                Quest quest = PixelBuiltQuests.getConfig().getQuestFor(new Pair<>(line, i));
                                return quest != null && quest.oneTime;
                            })
                            .collect(Collectors.toList());
                    final User u = user;
                    quests.forEach(i -> PixelBuiltQuests.getStorage().resetQuest(u.getUniqueId(), Quest.of(line, i)));
                    sender.sendMessage(Text.of(
                            TextColors.GREEN, "Successfully resetted oneTime quests!"
                    ));
                    return CommandResult.success();
                })
                .build();
        Sponge.getCommandManager().register(PixelBuiltQuests.instance, resetOneTime, "resetOneTime");

        CommandSpec spec = CommandSpec.builder()
                .description(Text.of("Main command of the PixelBuilt-Quests plugin"))
                .permission("pbquests.admin")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("action"))),
                        GenericArguments.remainingJoinedStrings(Text.of("args")))
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        if (args.<String>getOne("action").isPresent() && args.<String>getOne("args").isPresent()) {
                            String action = args.<String>getOne("action").get();
                            String arg = args.<String>getOne("args").get();

                            BlockRay<World> blockRay = BlockRay.from((Player)src)
                                    .skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                                    .build();
                            Optional<BlockRayHit<World>> hitOpt = blockRay.end();
                            if(hitOpt.isPresent()) {

                                // Spawning the NPC
                                if(action.equalsIgnoreCase("spawnnpc")) {

                                    EntityType entity = EntityTypes.VILLAGER;

                                    if (Sponge.getRegistry().getType(EntityType.class, arg).isPresent()) {
                                        entity = Sponge.getRegistry().getType(EntityType.class, arg).get();
                                    }

                                    Living npc = (Living) hitOpt.get().getExtent()
                                            .createEntity(entity, hitOpt.get().getPosition());

                                    npc.offer(Keys.PERSISTS, true);
                                    npc.offer(Keys.AI_ENABLED, false);
                                    npc.offer(Keys.CUSTOM_NAME_VISIBLE, true);
                                    npc.offer(Keys.WALKING_SPEED, 0D);
                                    npc.offer(Keys.INFINITE_DESPAWN_DELAY, true);
                                    npc.offer(Keys.INVULNERABILITY_TICKS, Integer.MAX_VALUE);
                                    npc.offer(Keys.HAS_GRAVITY, false);

                                    hitOpt.get().getExtent().spawnEntity(npc);

                                } else if (action.equalsIgnoreCase("checkprogress")) {
                                    ChatUtils.waitForResponse((Player)src,"&a Type the Quest Line that you want to query", (player1, questLine) -> {
                                        player1.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&a Progress of &e" + arg + ": " + PixelBuiltQuests.getStorage()
                                                .getProgress(Sponge.getServer().getPlayer(arg).get().getUniqueId(), QuestLine.of(questLine))));
                                    });
                                } else if (action.equalsIgnoreCase("setprogress")) {
                                    ChatUtils.waitForResponse((Player)src, "&a Type the Quest Line that you want to set the progress of the player", (player, questLine) -> {
                                        ChatUtils.waitForResponse((Player)src, "&aType the progress that you want to set for " + arg, (player1, progress) -> {
                                            PixelBuiltQuests.getStorage().setProgress(Sponge.getServer().getPlayer(arg).get().getUniqueId(), QuestLine.of(questLine), Integer.parseInt(progress));
                                            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&a Sucessfully updated the progress level of &e" + arg + "&a to &e" + progress + "&a!"));
                                        });
                                    });
                                }

                            }
                        }

                        return CommandResult.success();
                    }
                })
                .build();
        Sponge.getCommandManager().register(PixelBuiltQuests.instance, spec, "pbq", "pbquests");

        CommandSpec setProgress = CommandSpec.builder()
                .permission("pbquests.admin.setprogress")
                .arguments(
                        GenericArguments.string(Text.of("line")),
                        GenericArguments.integer(Text.of("progress")),
                        GenericArguments.userOrSource(Text.of("player"))
                )
                .executor((sender, context) -> {
                    String line = context.<String>getOne("line").get();
                    int progress = context.<Integer>getOne("progress").get();
                    User user = context.<User>getOne("player").get();
                    PixelBuiltQuests.getStorage().setProgress(user.getUniqueId(), QuestLine.of(line), progress);
                    sender.sendMessage(Text.of(
                            TextColors.GREEN, "Sucessfully updated the progress level of ",
                            TextColors.YELLOW, user.getName(),
                            TextColors.GREEN, " to ",
                            TextColors.YELLOW, progress,
                            TextColors.GREEN, "!"
                    ));
                    return CommandResult.success();
                })
                .build();
        Sponge.getCommandManager().register(PixelBuiltQuests.instance, setProgress, "setProgress");

        CommandSpec checkProgress = CommandSpec.builder()
                .permission("pbquests.admin.checkprogress")
                .arguments(
                        GenericArguments.string(Text.of("line")),
                        GenericArguments.userOrSource(Text.of("player"))
                )
                .executor((sender, context) -> {
                    String line = context.<String>getOne("line").get();
                    User user = context.<User>getOne("player").get();
                    sender.sendMessage(Text.of(
                            TextColors.GREEN, "Progress of ",
                            TextColors.YELLOW, user.getName(),
                            TextColors.GREEN, ": ",
                            TextColors.YELLOW, PixelBuiltQuests.getStorage().getProgress(user.getUniqueId(), QuestLine.of(line))
                    ));
                    return CommandResult.success();
                })
                .build();
        Sponge.getCommandManager().register(PixelBuiltQuests.instance, checkProgress, "checkProgress");

        CommandSpec status = CommandSpec.builder()
                .permission("pbquests.user.status")
                .arguments(GenericArguments.string(Text.of("quest line")))
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("This command can only be used by players!"));
                    }
                    Player p = (Player) sender;

                    if (!Sponge.getPluginManager().isLoaded("pbqmessages")) {
                        throw new CommandException(Text.of("You must load PBQMessages if you want to use this command!"));
                    }

                    String line = context.<String>getOne("quest line").get();
                    int progress = PixelBuiltQuests.getStorage().getProgress(p.getUniqueId(), QuestLine.of(line));

                    List<String> messages = PBQMessages.getMessagesFor(line, progress);
                    if (messages != null && !messages.isEmpty()) {
                        messages.forEach(msg -> p.sendMessage(Util.toText(msg)));
                    }

                    return CommandResult.success();
                })
                .build();
        Sponge.getCommandManager().register(PixelBuiltQuests.instance, status, "status", "s", "st", "info");

    }

}
