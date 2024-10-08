package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ProgressSetCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("set")
                .requires(s -> Util.hasPermission(s, "pbq.command.progress.set"))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(QuestLineArgument.questLine("quest line")
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(ProgressSetCommand::execute))));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        QuestLine line = QuestLineArgument.verifyAndGetQuestLine(context, "quest line");
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        PixelBuiltQuests.storage().setProgress(player.getUUID(), line, amount);

        player.sendSystemMessage(
                Component.empty().withStyle(ChatFormatting.GREEN)
                        .append("Successfully set the progress of ")
                        .append(player.getName().getString())
                        .append(" on ")
                        .append(line.getName())
                        .append(" quest line to ")
                        .append(amount + "!")
        );

        return 1;
    }

}
