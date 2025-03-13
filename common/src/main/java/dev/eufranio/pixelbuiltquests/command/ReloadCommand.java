package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.events.PBQEvents;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("reload")
                .requires(s -> Util.hasPermission(s, "pbq.command.reload"))
                .executes(ReloadCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().sendSystemMessage(
                Component.literal("Reloading PixelBuiltQuests...").withStyle(ChatFormatting.GREEN));

        PixelBuiltQuests.instance().reload();

        context.getSource().sendSystemMessage(
                Component.literal("Successfully reloaded PixelBuiltQuests.").withStyle(ChatFormatting.GREEN));
        return 1;
    }

}
