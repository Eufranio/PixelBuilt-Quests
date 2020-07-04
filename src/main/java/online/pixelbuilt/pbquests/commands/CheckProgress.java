package online.pixelbuilt.pbquests.commands;

import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;

public class CheckProgress extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.requireOne("player");
        QuestLine line = args.requireOne("quest line");

        PlayerData data = storageManager.getData(user.getUniqueId());
        src.sendMessage(Util.toText(ConfigManager.getConfig().messages.currentProgress
                .replace("%player%", user.getName())
                .replace("%line%", line.getName())
                .replace("%progress%", data.getProgress(line) + "")
        ));

        return CommandResult.success();
    }
}
