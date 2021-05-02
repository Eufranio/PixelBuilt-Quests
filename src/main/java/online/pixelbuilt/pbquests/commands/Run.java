package online.pixelbuilt.pbquests.commands;

import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class Run extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        QuestLine line = args.requireOne("quest line");
        Quest quest = args.requireOne("quest");
        Player player = args.<Player>getOne("player").orElse(null);
        if (player == null) {
            if (!(src instanceof Player))
                throw new CommandException(Text.of(
                        "You must specify a player to run this command when running it from console!"
                ));
            player = (Player) src;
        }
        quest.getExecutor().execute(quest, line, player);
        return CommandResult.success();
    }
}
