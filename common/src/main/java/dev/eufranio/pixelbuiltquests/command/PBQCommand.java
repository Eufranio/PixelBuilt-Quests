package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PBQCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("pbq")
                .requires(s -> Util.hasPermission(s, "pbq.command.base"))
                .then(ProgressCommand.command())
                .then(QuestRunCommand.command())
                .then(StatusCommand.command())
                .then(TriggerCommand.command())
                .then(ReloadCommand.command());
    }

}
