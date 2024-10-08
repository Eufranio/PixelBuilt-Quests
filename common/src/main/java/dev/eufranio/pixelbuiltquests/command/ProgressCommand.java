package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.command.argument.QuestLineArgument;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ProgressCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("progress")
                .requires(s -> Util.hasPermission(s, "pbq.command.progress.base"))
                .then(ProgressCheckCommand.command())
                .then(ProgressAddCommand.command())
                .then(ProgressSetCommand.command())
                .then(QuestLineArgument.questLine("quest line")
                        .requires(s -> Util.hasPermission(s, "pbq.command.progress.own"))
                        .executes(ProgressCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            QuestLine line = QuestLineArgument.verifyAndGetQuestLine(context, "quest line");
            ServerPlayer player = context.getSource().getPlayerOrException();

            int progress = PixelBuiltQuests.storage().getProgress(player.getUUID(), line);

            player.sendSystemMessage(
                    Component.empty().withStyle(ChatFormatting.GREEN)
                            .append("Your progress on the ")
                            .append(line.getName())
                            .append(" quest line: ")
                            .append(Component.literal(progress+"").withStyle(ChatFormatting.YELLOW)));
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(Component.empty().withStyle(ChatFormatting.RED).append("Invalid command."));
            return 1;
        }

        return 1;
    }

}
