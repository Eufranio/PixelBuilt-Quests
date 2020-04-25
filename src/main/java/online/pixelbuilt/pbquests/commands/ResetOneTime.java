package online.pixelbuilt.pbquests.commands;

import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ResetOneTime extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        QuestLine line = args.requireOne("quest line");
        User user = args.requireOne("player");

        PlayerData data = storageManager.getData(user.getUniqueId());
        data.resetQuestLine(line);
        src.sendMessage(Text.of(
                TextColors.GREEN, "Successfully resetted oneTime quests!"
        ));
        storageManager.save(data);

        return CommandResult.success();
    }
}
