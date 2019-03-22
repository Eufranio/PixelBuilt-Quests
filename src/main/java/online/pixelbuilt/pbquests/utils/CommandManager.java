package online.pixelbuilt.pbquests.utils;

import com.google.common.collect.Lists;
import io.github.eufranio.pbqmessages.PBQMessages;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.config.Trigger;
import online.pixelbuilt.pbquests.task.TaskTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.*;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by Frani on 06/09/2017.
 */
public class CommandManager {

    public static void registerCommands() {
        CommandSpec resetOneTime = CommandSpec.builder()
                .description(Text.of("Resets one time quests of a specific quest line"))
                .permission("pbq.command.reset")
                .arguments(
                        QuestLineElement.create(Text.of("quest line")),
                        GenericArguments.optional(
                                GenericArguments.user(Text.of("player"))
                        )
                )
                .executor((sender, context) -> {
                    QuestLine line = context.<QuestLine>getOne("quest line").get();
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
                            .filter(s -> s.split(",")[0].equalsIgnoreCase(line.getName()))
                            .map(s -> Integer.parseInt(s.split(",")[1]))
                            .filter(i -> {
                                Quest quest = ConfigManager.getQuest(i);
                                return quest != null && quest.tasks.stream().anyMatch(e -> e.type == TaskTypes.ONE_TIME);
                            })
                            .collect(Collectors.toList());
                    final User u = user;
                    quests.forEach(i -> PixelBuiltQuests.getStorage().resetQuest(u.getUniqueId(), line, i));
                    sender.sendMessage(Text.of(
                            TextColors.GREEN, "Successfully resetted oneTime quests!"
                    ));
                    return CommandResult.success();
                })
                .build();

        CommandSpec spawn = CommandSpec.builder()
                .permission("pbq.command.spawn")
                .arguments(GenericArguments.catalogedElement(Text.of("type"), EntityType.class))
                .executor((sender, context) -> {
                    BlockRay<World> blockRay = BlockRay.from((Player) sender)
                            .skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                            .build();
                    Optional<BlockRayHit<World>> hitOpt = blockRay.end();
                    if(hitOpt.isPresent()) {
                        EntityType entity = context.<EntityType>getOne("type").get();

                        Entity npc = hitOpt.get().getExtent().createEntity(entity, hitOpt.get().getPosition());
                        npc.offer(Keys.PERSISTS, true);
                        npc.offer(Keys.AI_ENABLED, false);
                        npc.offer(Keys.CUSTOM_NAME_VISIBLE, true);
                        npc.offer(Keys.INVULNERABILITY_TICKS, Integer.MAX_VALUE);
                        npc.offer(Keys.INVULNERABLE, true);
                        npc.offer(Keys.HAS_GRAVITY, false);
                        hitOpt.get().getExtent().spawnEntity(npc);

                        sender.sendMessage(Text.of(
                                TextColors.GREEN, "Successfully spawned NPC! Assign a quest to it now!"
                        ));
                    } else {
                        throw new CommandException(Text.of("You're not looking to a block!"));
                    }

                    return CommandResult.success();
                })
                .build();

        CommandSpec checkProgress = CommandSpec.builder()
                .permission("pbq.command.checkprogress")
                .arguments(
                        GenericArguments.userOrSource(Text.of("player")),
                        QuestLineElement.create(Text.of("quest line"))
                )
                .executor((sender, context) -> {
                    User user = context.<User>getOne("player").get();
                    QuestLine line = context.<QuestLine>getOne("quest line").get();
                    int progress = PixelBuiltQuests.getStorage().getProgress(user.getUniqueId(), line);
                    sender.sendMessage(Text.of(
                            TextColors.GREEN, "Progress of ",
                            TextColors.YELLOW, user.getName(),
                            TextColors.GREEN, ": ",
                            TextColors.YELLOW, progress
                    ));

                    return CommandResult.success();
                })
                .build();

        CommandSpec setProgress = CommandSpec.builder()
                .permission("pbq.command.setprogress")
                .arguments(
                        GenericArguments.userOrSource(Text.of("player")),
                        QuestLineElement.create(Text.of("quest line")),
                        GenericArguments.integer(Text.of("progress"))
                )
                .executor((sender, context) -> {
                    User user = context.<User>getOne("player").get();
                    QuestLine line = context.<QuestLine>getOne("quest line").get();
                    int progress = context.<Integer>getOne("progress").get();

                    PixelBuiltQuests.getStorage().setProgress(user.getUniqueId(), line, progress);
                    sender.sendMessage(Text.of(
                            TextColors.GREEN, "Successfully updated progress of ",
                            TextColors.YELLOW, user.getName(),
                            TextColors.GREEN, " to ",
                            TextColors.YELLOW, progress,
                            TextColors.GREEN, "!"
                    ));
                    return CommandResult.success();
                })
                .build();

        CommandSpec status = CommandSpec.builder()
                .permission("pbq.command.status")
                .arguments(QuestLineElement.create(Text.of("quest line")))
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("This command can only be used by players!"));
                    }
                    Player p = (Player) sender;

                    if (!Sponge.getPluginManager().isLoaded("pbqmessages")) {
                        throw new CommandException(Text.of("You must load PBQMessages if you want to use this command!"));
                    }

                    QuestLine line = context.<QuestLine>getOne("quest line").get();
                    int progress = PixelBuiltQuests.getStorage().getProgress(p.getUniqueId(), line);

                    List<String> messages = PBQMessages.getMessagesFor(line.getName(), progress);
                    if (messages != null && !messages.isEmpty()) {
                        messages.forEach(msg -> p.sendMessage(Util.toText(msg)));
                    }

                    return CommandResult.success();
                })
                .build();

        CommandSpec addQuest = CommandSpec.builder()
                .permission("pbq.command.addquest")
                .arguments(
                        GenericArguments.firstParsing(
                                GenericArguments.seq(
                                        QuestElement.create(Text.of("quest")),
                                        QuestLineElement.create(Text.of("quest line")),
                                        GenericArguments.enumValue(Text.of("walk/click"), Trigger.Type.class)
                                ),
                                GenericArguments.seq(
                                        QuestElement.create(Text.of("quest")),
                                        QuestLineElement.create(Text.of("quest line"))
                                )
                        )
                )
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("You must be a player to run this command!"));
                    }

                    Player p = (Player) sender;
                    Quest quest = context.<Quest>getOne("quest").get();
                    QuestLine line = context.<QuestLine>getOne("quest line").get();
                    Optional<Trigger.Type> optType = context.getOne("walk/click");
                    if (optType.isPresent()) {
                        // block trigger
                        Location<World> loc = p.getLocation().sub(0, 1, 0);
                        Trigger trigger = new Trigger(loc, quest, line, optType.get());
                        PixelBuiltQuests.getStorage().addTrigger(trigger);
                        sender.sendMessage(Text.of(
                                TextColors.GREEN, "Successfully added trigger at ", Util.locationToText(trigger.getLocation())
                        ));
                    } else {
                        sender.sendMessage(Text.of(
                                TextColors.GRAY, "Right click the entity that you want to assign this quest to!"
                        ));

                        Sponge.getEventManager().registerListeners(PixelBuiltQuests.instance, new OneTimeHandler((e, player) -> {
                            if (player.getUniqueId().equals(p.getUniqueId())) {
                                PixelBuiltQuests.getStorage().addNPC(e.getTargetEntity(), line, quest.getId());
                                sender.sendMessage(Text.of(
                                        TextColors.GREEN, "Successfully added NPC!"
                                ));
                            }
                        }));
                    }

                    return CommandResult.success();
                })
                .build();

        CommandSpec delete = CommandSpec.builder()
                .permission("pbq.command.delete")
                .arguments(GenericArguments.string(Text.of("block/npc")))
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("You must be a player to run this!"));
                    }
                    Player p = (Player) sender;

                    String type = context.<String>getOne("block/npc").get();
                    if (type.equalsIgnoreCase("block")) {
                        BlockRay<World> blockRay = BlockRay.from((Player) sender)
                                .skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                                .build();
                        Optional<BlockRayHit<World>> hitOpt = blockRay.end();
                        if (hitOpt.isPresent()) {
                            Trigger trigger = PixelBuiltQuests.getStorage().getTriggerAt(hitOpt.get().getLocation());
                            if (trigger == null) {
                                throw new CommandException(Text.of("Invalid trigger!"));
                            }

                            PixelBuiltQuests.getStorage().removeTrigger(trigger);
                            sender.sendMessage(Text.of(
                                    TextColors.GREEN, "Successfully removed trigger!"
                            ));
                        } else {
                            throw new CommandException(Text.of("You're not looking to a block!"));
                        }

                    } else if (type.equalsIgnoreCase("npc")) {
                        sender.sendMessage(Text.of(
                                TextColors.GRAY, "Right click the entity the entity that you want to remove the trigger!"
                        ));

                        Sponge.getEventManager().registerListeners(PixelBuiltQuests.instance, new OneTimeHandler((e, player) -> {
                            if (player.getUniqueId().equals(p.getUniqueId())) {
                                Tuple<Quest, QuestLine> quest = PixelBuiltQuests.getStorage().getQuest(e.getTargetEntity());
                                if (quest == null) {
                                    player.sendMessage(Text.of(
                                            TextColors.GREEN, "There's no quest associated with this NPC!"
                                    ));
                                } else {
                                    PixelBuiltQuests.getStorage().removeNPC(e.getTargetEntity());
                                    player.sendMessage(Text.of(
                                            TextColors.GREEN, "Successfully removed quest from this entity!"
                                    ));
                                }
                            }
                        }));
                    } else {
                        throw new CommandException(Text.of("Unknown option! Specify block or npc!"));
                    }

                    return CommandResult.success();
                })
                .build();

        CommandSpec rename = CommandSpec.builder()
                .permission("pbq.command.rename")
                .arguments(GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, true))
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("Only players can run this command!"));
                    }
                    Player p = (Player) sender;

                    Text name = context.<Text>getOne("name").get();
                    p.sendMessage(Text.of(
                            TextColors.GREEN, "Click in the entity that you want to rename!"
                    ));
                    Sponge.getEventManager().registerListeners(PixelBuiltQuests.instance, new OneTimeHandler((e, player) -> {
                        if (player.getUniqueId().equals(p.getUniqueId())) {
                            e.getTargetEntity().offer(Keys.DISPLAY_NAME, name);
                            player.sendMessage(Text.of(
                                    TextColors.GREEN, "Successfully renamed entity!"
                            ));
                        }
                    }));

                    return CommandResult.success();
                })
                .build();

        CommandSpec main = CommandSpec.builder()
                .permission("pbq.command.main")
                .executor((sender, context) -> {
                    List<Text> text = Lists.newArrayList(
                            cmd("pbq info <quest line>", "Shows your progress status in the specified quest line"),
                            cmd("pbq setProgress <quest line> <progress> [<player>]", "Sets the progress of the player in the specified quest line"),
                            cmd("pbq checkProgress [<player>] <quest line>", "Checks the progress of the player in the specific quest line"),
                            cmd("pbq spawn <type>", "Spawns an NPC of the specified type. Use minecraft:villager if not sure"),
                            cmd("pbq resetOneTime <quest line> [<player>]", "Resets the status of the one time quests of the player in the specified quest line"),
                            cmd("pbq addQuest [<quest id> <quest line> <walk/click>], [<quest id> <quest line>]", "Adds a quest to a block/entity. If walk/click is specified, it will add the quest to the target " +
                                    "block. If not, it will add the quest to the next clicked entity"),
                            cmd("pbq delete <block/npc>", "Deletes the quest of the target block/entity"),
                            cmd("pbq rename <name>", "Renames the next clicked entity to the specified name")
                    );

                    PaginationList.builder()
                            .title(Text.of(TextColors.YELLOW, "PBQ Commands"))
                            .linesPerPage(19)
                            .contents(text)
                            .sendTo(sender);

                    return CommandResult.success();
                })
                .child(status, "status", "info")
                .child(setProgress, "setProgress")
                .child(checkProgress, "checkProgress")
                .child(spawn, "spawn", "spawnnpc")
                .child(resetOneTime, "resetOneTime")
                .child(addQuest, "addQuest")
                .child(delete, "delete")
                .child(rename, "rename")
                .build();
        Sponge.getCommandManager().register(PixelBuiltQuests.instance, main, "pbq", "pbquests");
    }

    private static Text cmd(String command, String desc) {
        return Text.of(TextColors.YELLOW, "/" + command, TextColors.GRAY, " - ", TextColors.WHITE, desc);
    }

    private static class QuestLineElement extends CommandElement {

        static QuestLineElement create(Text key) {
            return new QuestLineElement(key);
        }

        private QuestLineElement(Text key) {
            super(key);
        }

        @Nullable
        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            String line = args.next();
            QuestLine l = ConfigManager.getLine(line);
            if (l == null) {
                throw args.createError(Text.of("Invalid quest line!"));
            }
            return l;
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return ConfigManager.getLines().stream().map(QuestLine::getName).collect(Collectors.toList());
        }

        @Override
        public Text getUsage(CommandSource src) {
            return Text.of("<quest line>");
        }
    }

    private static class QuestElement extends CommandElement {

        public static QuestElement create(Text key) {
            return new QuestElement(key);
        }

        private QuestElement(Text key) {
            super(key);
        }

        @Nullable
        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            try {
                int id = Integer.parseInt(args.next());
                Quest quest = ConfigManager.getQuest(id);
                if (quest == null) {
                    throw args.createError(Text.of("Invalid quest!"));
                }
                return quest;
            } catch(NumberFormatException e) {
                throw args.createError(Text.of("Invalid id!"));
            }
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return ConfigManager.getQuests().stream().map(Quest::getId).map(String::valueOf).collect(Collectors.toList());
        }

        @Override
        public Text getUsage(CommandSource src) {
            return Text.of("<quest id>");
        }
    }

    public static class OneTimeHandler {

        private BiConsumer<InteractEntityEvent, Player> func;
        OneTimeHandler(BiConsumer<InteractEntityEvent, Player> function) {
            this.func = function;
        }

        @Listener(beforeModifications = true, order = Order.FIRST)
        public void onRightClick(InteractEntityEvent.Secondary.MainHand event, @Root Player player) {
            this.func.accept(event, player);
            event.setCancelled(true);
            Sponge.getEventManager().unregisterListeners(this);
        }

    }

}
