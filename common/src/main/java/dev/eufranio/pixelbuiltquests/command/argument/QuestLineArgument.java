package dev.eufranio.pixelbuiltquests.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class QuestLineArgument {

    public static RequiredArgumentBuilder<CommandSourceStack, String> questLine(String arg) {
        return Commands.argument(arg, StringArgumentType.string())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                        PixelBuiltQuests.registry().allOfType(QuestLine.class).stream().map(QuestLine::getId),
                        builder));
    }

    public static QuestLine verifyAndGetQuestLine(CommandContext<?> context, String name) throws CommandSyntaxException {
        String questLineId = StringArgumentType.getString(context, name);
        QuestLine questLine = PixelBuiltQuests.registry().get(QuestLine.class, questLineId);
        if (questLine == null) {
            throw new SimpleCommandExceptionType(Component.literal("Invalid quest line id.")).create();
        }
        return questLine;
    }

}
