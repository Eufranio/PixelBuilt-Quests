package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class TriggerListCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("list")
                .requires(s -> Util.hasPermission(s, "pbq.command.trigger.list"))
                .executes(TriggerListCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        List<MutableComponent> text = PixelBuiltQuests.triggerManager().getTriggers().stream()
                .map(trigger -> {
                    MutableComponent teleport = Component.literal("[TP]")
                            .withStyle(ChatFormatting.GREEN)
                            .withStyle(style ->
                                    style.withHoverEvent(new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT,
                                            Component.literal("Click to teleport")))
                                    .withClickEvent(new ClickEvent(
                                            ClickEvent.Action.RUN_COMMAND,
                                            "/pbq trigger teleport " + trigger.getId()))
                            );

                    MutableComponent delete = Component.literal("[Remove]")
                            .withStyle(ChatFormatting.RED)
                            .withStyle(style ->
                                    style.withHoverEvent(new HoverEvent(
                                                    HoverEvent.Action.SHOW_TEXT,
                                                    Component.literal("Click to delete")))
                                            .withClickEvent(new ClickEvent(
                                                    ClickEvent.Action.RUN_COMMAND,
                                                    "/pbq trigger delete " + trigger.getId()))
                            );

                    return Component.empty().withStyle(ChatFormatting.GRAY)
                            .append(trigger.getId() + " - ")
                            .append(Component.literal(trigger.getQuest().getQuestLine().getId() + "/" + trigger.getQuest().getQuest().getId()).withStyle(ChatFormatting.GREEN))
                            .append(" - ")
                            .append(Component.literal(trigger.getType().getName()).withStyle(ChatFormatting.YELLOW))
                            .append(" - cancel: ")
                            .append(Component.literal(trigger.shouldCancelOriginalAction()+"").withStyle(trigger.shouldCancelOriginalAction() ? ChatFormatting.GREEN : ChatFormatting.RED))
                            .append(" - ")
                            .append(teleport)
                            .append(" ")
                            .append(delete);
                })
                .toList();

        player.sendSystemMessage(Component.literal("PBQ Triggers").withStyle(ChatFormatting.AQUA));
        text.forEach(player::sendSystemMessage);

        return 1;
    }

}
