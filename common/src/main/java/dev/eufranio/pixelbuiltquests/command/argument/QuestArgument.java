package dev.eufranio.pixelbuiltquests.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.Quest;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class QuestArgument {

    public static RequiredArgumentBuilder<CommandSourceStack, String> quest(String arg) {
        return Commands.argument(arg, StringArgumentType.string())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                        PixelBuiltQuests.registry().allOfType(Quest.class).stream().map(Quest::getId),
                        builder));
    }

    public static Quest verifyAndGetQuest(CommandContext<?> context, String name) throws CommandSyntaxException {
        String questId = StringArgumentType.getString(context, name);
        Quest quest = PixelBuiltQuests.registry().get(Quest.class, questId);
        if (quest == null) {
            throw new SimpleCommandExceptionType(Component.literal("Invalid quest id.")).create();
        }
        return quest;
    }

}
