package online.pixelbuilt.pbquests.utils;

import com.google.common.collect.Lists;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.commands.*;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.Trigger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.*;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nullable;
import java.util.List;
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
                        GenericArguments.userOrSource(Text.of("player"))
                )
                .executor(new ResetOneTime())
                .build();

        CommandSpec spawn = CommandSpec.builder()
                .permission("pbq.command.spawn")
                .arguments(GenericArguments.catalogedElement(Text.of("type"), EntityType.class))
                .executor(new Spawn())
                .build();

        CommandSpec checkProgress = CommandSpec.builder()
                .permission("pbq.command.checkprogress")
                .arguments(
                        GenericArguments.userOrSource(Text.of("player")),
                        QuestLineElement.create(Text.of("quest line"))
                )
                .executor(new CheckProgress())
                .build();

        CommandSpec setProgress = CommandSpec.builder()
                .permission("pbq.command.setprogress")
                .arguments(
                        GenericArguments.userOrSource(Text.of("player")),
                        QuestLineElement.create(Text.of("quest line")),
                        GenericArguments.integer(Text.of("progress"))
                )
                .executor(new SetProgress())
                .build();

        /*CommandSpec status = CommandSpec.builder()
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
                .build();*/

        CommandSpec addQuest = CommandSpec.builder()
                .permission("pbq.command.addquest")
                .arguments(
                        QuestLineElement.create(Text.of("quest line")),
                        QuestElement.create(Text.of("quest")),
                        GenericArguments.enumValue(Text.of("type"), Trigger.Type.class)
                )
                .executor(new AddQuest())
                .build();

        CommandSpec delete = CommandSpec.builder()
                .permission("pbq.command.delete")
                .arguments(GenericArguments.string(Text.of("block/npc")))
                .executor(new Delete())
                .build();

        CommandSpec rename = CommandSpec.builder()
                .permission("pbq.command.rename")
                .arguments(GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, true))
                .executor(new Rename())
                .build();

        CommandSpec status = CommandSpec.builder()
                .permission("pbq.command.status")
                .executor(new Status())
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
                            cmd("pbq addQuest <quest id> <quest line> <type>", "Adds a quest to a block/entity. The type can be walk, click or npc"),
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

}
