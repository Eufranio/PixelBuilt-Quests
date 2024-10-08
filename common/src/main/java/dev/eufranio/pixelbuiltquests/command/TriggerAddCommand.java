package dev.eufranio.pixelbuiltquests.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.command.argument.QuestArgument;
import dev.eufranio.pixelbuiltquests.command.argument.QuestLineArgument;
import dev.eufranio.pixelbuiltquests.command.argument.TriggerTypeArgument;
import dev.eufranio.pixelbuiltquests.quest.Quest;
import dev.eufranio.pixelbuiltquests.quest.QuestLine;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TriggerAddCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("add")
                .requires(s -> Util.hasPermission(s, "pbq.command.trigger.add"))
                .then(TriggerTypeArgument.triggerType("trigger type")
                        .then(QuestLineArgument.questLine("quest line")
                                .then(QuestArgument.quest("quest")
                                        .then(Commands.argument("cancel action", BoolArgumentType.bool())
                                                .executes(TriggerAddCommand::execute)))));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TriggerType type = TriggerTypeArgument.verifyAndGet(context, "trigger type");
        QuestLine line = QuestLineArgument.verifyAndGetQuestLine(context, "quest line");
        Quest quest = QuestArgument.verifyAndGetQuest(context, "quest");
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean cancelOriginalAction = BoolArgumentType.getBool(context, "cancel action");

        type.getTriggerCreator().createNewTrigger(player, type, QuestReference.of(line, quest), cancelOriginalAction)
                .thenAccept(trigger -> {
                    PixelBuiltQuests.triggerManager().saveTrigger(trigger);
                    player.sendSystemMessage(Component.empty().withStyle(ChatFormatting.GREEN)
                            .append("Successfully created a new ")
                            .append(type.getName())
                            .append(" trigger!")
                    );
                });
        return 1;
    }

}
