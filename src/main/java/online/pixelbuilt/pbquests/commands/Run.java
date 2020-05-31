package online.pixelbuilt.pbquests.commands;

import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class Run extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        QuestLine line = args.requireOne("quest line");
        Quest quest = args.requireOne("quest");
        quest.getExecutor().execute(quest, line, args.<Player>getOne("player").orElse((Player) src));
        return CommandResult.success();
    }
}
