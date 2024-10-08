package dev.eufranio.pixelbuiltquests.command.argument;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.storage.sql.Trigger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class TriggerArgument {

    public static RequiredArgumentBuilder<CommandSourceStack, Integer> trigger(String arg) {
        return Commands.argument(arg, IntegerArgumentType.integer())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                        PixelBuiltQuests.triggerManager().getTriggers()
                                .stream()
                                .map(trigger -> trigger.getId()+"")
                                .toList(),
                        builder));
    }

    public static Trigger verifyAndGet(CommandContext<?> context, String name) throws CommandSyntaxException {
        int id = IntegerArgumentType.getInteger(context, name);
        Trigger trigger = PixelBuiltQuests.triggerManager().getTriggers().stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
        if (trigger == null) {
            throw new SimpleCommandExceptionType(Component.literal("Invalid trigger.")).create();
        }
        return trigger;
    }

}
