package dev.eufranio.pixelbuiltquests.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class TriggerTypeArgument {

    public static RequiredArgumentBuilder<CommandSourceStack, String> triggerType(String arg) {
        return Commands.argument(arg, StringArgumentType.string())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                        PixelBuiltQuests.registry().allOfType(TriggerType.class)
                                .stream()
                                .map(TriggerType::getId)
                                .map(id -> "\"" + id + "\""),
                        builder));
    }

    public static TriggerType verifyAndGet(CommandContext<?> context, String name) throws CommandSyntaxException {
        String id = StringArgumentType.getString(context, name);
        TriggerType trigger = PixelBuiltQuests.registry().get(TriggerType.class, id);
        if (trigger == null) {
            throw new SimpleCommandExceptionType(Component.literal("Invalid trigger type.")).create();
        }
        return trigger;
    }

}
