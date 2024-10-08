package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.command.argument.TriggerArgument;
import dev.eufranio.pixelbuiltquests.storage.sql.Trigger;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TriggerDeleteCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("delete")
                .requires(s -> Util.hasPermission(s, "pbq.command.trigger.delete"))
                .then(TriggerArgument.trigger("trigger")
                        .executes(TriggerDeleteCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Trigger trigger = TriggerArgument.verifyAndGet(context, "trigger");
        PixelBuiltQuests.triggerManager().delete(trigger);
        player.sendSystemMessage(Component.literal("Successfully deleted trigger.").withStyle(ChatFormatting.GREEN));
        return 1;
    }

}
