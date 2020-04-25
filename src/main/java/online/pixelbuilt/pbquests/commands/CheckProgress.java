package online.pixelbuilt.pbquests.commands;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CheckProgress extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.requireOne("player");
        QuestLine line = args.requireOne("quest line");

        PlayerData data = storageManager.getData(user.getUniqueId());
        src.sendMessage(Text.of(
                TextColors.GREEN, "Progress of ",
                TextColors.YELLOW, user.getName(),
                TextColors.GREEN, ": ",
                TextColors.YELLOW, data.getProgress(line)
        ));

        return CommandResult.success();
    }
}
