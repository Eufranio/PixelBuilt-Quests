package online.pixelbuilt.pbquests.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.stream.Collectors;

public class Triggers extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> text = storageManager.getTriggers().values().stream()
                .map(trigger -> {
                    Text teleport = Text.of(
                            TextActions.executeCallback(s -> {
                                if (trigger.location == null) {
                                    s.sendMessage(Text.of(TextColors.RED, "This trigger has an invalid location!"));
                                    return;
                                }
                                ((Player) s).setLocation(trigger.location);
                            }),
                            TextActions.showText(Text.of("Click to teleport")),
                            TextColors.GREEN, "[TP]"
                    );
                    Text delete = Text.of(
                            TextActions.executeCallback(s -> {
                                storageManager.removeTrigger(trigger);
                                s.sendMessage(Text.of(TextColors.GREEN, "Successfully removed trigger!"));
                            }),
                            TextActions.showText(Text.of("Click to remove")),
                            TextColors.RED, "[Remove]"
                    );
                    return Text.of(TextColors.GRAY,
                            trigger.npc.toString().substring(0, 4), "... - ",
                            Text.of(TextColors.GREEN, trigger.getQuestLine().getName(), "/", trigger.getQuest().getId()),
                            " - ",
                            Text.of(TextColors.YELLOW, trigger.type),
                            " - cancel: ",
                            Text.of(trigger.cancelOriginalAction ? TextColors.GREEN : TextColors.RED, trigger.cancelOriginalAction),
                            " - ",
                            teleport, " ", delete);
                })
                .collect(Collectors.toList());

        PaginationList.builder()
                .contents(text)
                .title(Text.of(TextColors.AQUA, "PBQ Triggers"))
                .padding(Text.of(TextColors.WHITE, TextStyles.STRIKETHROUGH, "-"))
                .sendTo(src);

        return CommandResult.success();
    }

}
