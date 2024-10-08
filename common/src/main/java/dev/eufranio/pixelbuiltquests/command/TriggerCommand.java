package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TriggerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("trigger")
                .requires(s -> Util.hasPermission(s, "pbq.command.trigger.base"))
                .then(TriggerAddCommand.command())
                .then(TriggerDeleteCommand.command())
                .then(TriggerListCommand.command())
                .then(TriggerTeleportCommand.command());
    }

}
