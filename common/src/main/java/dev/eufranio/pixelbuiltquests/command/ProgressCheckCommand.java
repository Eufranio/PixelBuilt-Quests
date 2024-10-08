package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.command.argument.QuestLineArgument;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ProgressCheckCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("check")
                .requires(s -> Util.hasPermission(s, "pbq.command.progress.check"))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(QuestLineArgument.questLine("quest line")
                                .executes(ProgressCheckCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        QuestLine line = QuestLineArgument.verifyAndGetQuestLine(context, "quest line");
        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        int progress = PixelBuiltQuests.storage().getProgress(player.getUUID(), line);

        player.sendSystemMessage(Util.text(ConfigManager.getConfig().messages.currentProgress
                .replace("%player%", player.getName().getString())
                .replace("%line%", line.getName())
                .replace("%progress%", progress + "")));

        return 1;
    }

}
