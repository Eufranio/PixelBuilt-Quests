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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class TriggerTeleportCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("teleport")
                .requires(s -> Util.hasPermission(s, "pbq.command.trigger.teleport"))
                .then(TriggerArgument.trigger("trigger")
                        .executes(TriggerTeleportCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Trigger trigger = TriggerArgument.verifyAndGet(context, "trigger");

        ServerLevel world = PixelBuiltQuests.server().getLevel(trigger.getWorld());
        if (world == null) {
            player.sendSystemMessage(Component.literal("World not found.").withStyle(ChatFormatting.RED));
            return 0;
        }

        player.teleportTo(world,
                trigger.getPos().getX(),
                trigger.getPos().getY() + 1,
                trigger.getPos().getZ(),
                player.getYRot(),
                player.getXRot());

        player.sendSystemMessage(Component.literal("Successfully teleported to the trigger").withStyle(ChatFormatting.GREEN));
        return 1;
    }

}
