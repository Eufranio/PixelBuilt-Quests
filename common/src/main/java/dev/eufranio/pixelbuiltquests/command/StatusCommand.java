package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.storage.sql.TaskStatus;
import dev.eufranio.pixelbuiltquests.task.AmountTask;
import dev.eufranio.pixelbuiltquests.task.QuestTaskReference;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class StatusCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("status")
                .requires(s -> Util.hasPermission(s, "pbq.command.status"))
                .executes(StatusCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        List<MutableComponent> text = new ArrayList<>();

        PixelBuiltQuests.storage().getPlayerData(player.getUUID())
                .getQuestsStarted()
                .forEach(quest -> {
                    var ref = quest.getReference();
                    text.add(Component.empty()
                            .append(Component.literal("* " + ref.getQuestLine().getName()).withStyle(ChatFormatting.YELLOW))
                            .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(ref.getQuest().getName()).withStyle(ChatFormatting.YELLOW)));

                    ref.getQuest().getTasks().forEach(task -> {
                        Component taskText;

                        if (task instanceof AmountTask amountTask) {
                            TaskStatus status = PixelBuiltQuests.storage().getOrCreateTaskStatus(
                                    player.getUUID(),
                                    QuestTaskReference.of(ref, amountTask));

                            taskText = Component.empty()
                                    .append(Component.literal(status.getValue() + "").withStyle(ChatFormatting.GREEN))
                                    .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                                    .append(Component.literal(amountTask.getTotal() + "").withStyle(ChatFormatting.GREEN))
                                    .append(Component.literal(" (").withStyle(ChatFormatting.LIGHT_PURPLE))
                                    .append(Component.literal(amountTask.getPercentageCompleted(status) + "").withStyle(ChatFormatting.LIGHT_PURPLE))
                                    .append(Component.literal("%)").withStyle(ChatFormatting.LIGHT_PURPLE));
                        } else {
                            if (task.isCompleted(player.getUUID(), ref)) {
                                taskText = Util.text(ConfigManager.getConfig().messages.taskCompleted);
                            } else {
                                taskText = Util.text(ConfigManager.getConfig().messages.taskNotCompleted);
                            }
                        }

                        text.add(Component.empty()
                                .append(Component.literal("  > ").withStyle(ChatFormatting.YELLOW))
                                .append(task.toText())
                                .append(Component.literal(" | ").withStyle(ChatFormatting.GRAY))
                                .append(taskText));
                    });

                    text.add(Component.empty());
                });

        if (text.isEmpty())
            text.add(Util.text(ConfigManager.getConfig().messages.noQuestsStarted));
        else
            text.removeLast();

        player.sendSystemMessage(Util.text(ConfigManager.getConfig().messages.playerQuestInfo
                .replace("%player%", player.getName().getString())));
        text.forEach(player::sendSystemMessage);

        return 1;
    }

}
