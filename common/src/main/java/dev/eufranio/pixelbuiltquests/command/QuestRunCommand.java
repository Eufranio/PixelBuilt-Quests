package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.eufranio.pixelbuiltquests.command.argument.QuestArgument;
import dev.eufranio.pixelbuiltquests.command.argument.QuestLineArgument;
import dev.eufranio.pixelbuiltquests.quest.Quest;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class QuestRunCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("run")
                .requires(s -> Util.hasPermission(s, "pbq.command.quest.run"))
                .then(QuestLineArgument.questLine("quest line")
                        .then(QuestArgument.quest("quest")
                                .executes(QuestRunCommand::execute)
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(QuestRunCommand::execute))));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        QuestLine line = QuestLineArgument.verifyAndGetQuestLine(context, "quest line");
        Quest quest = QuestArgument.verifyAndGetQuest(context, "quest");

        ServerPlayer player;
        try {
            player = EntityArgument.getPlayer(context, "player");
        } catch (IllegalArgumentException e) {
            player = context.getSource().getPlayer();
        }

        if (player == null) {
            throw new SimpleCommandExceptionType(() ->
                    "You must specify a player if running this command from console!").create();
        }

        QuestReference questReference = QuestReference.of(line, quest);

        player.sendSystemMessage(
                Component.empty().withStyle(ChatFormatting.GREEN)
                        .append("Running quest ")
                        .append(quest.getName())
                        .append(" for ")
                        .append(player.getName())
        );

        quest.getExecutor().execute(player, questReference);

        return 1;
    }

}
