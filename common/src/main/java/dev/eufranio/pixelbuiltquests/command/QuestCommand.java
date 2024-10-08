package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class QuestCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("quest")
                .then(QuestRunCommand.command());
    }

}
