package online.pixelbuilt.pbquests.commands;

import com.google.common.collect.Lists;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.AmountTask;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;

public class Status extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Only players can run this command!"));
        }

        PlayerData data = storageManager.getData(((Player) src).getUniqueId());
        List<Text> text = Lists.newArrayList();

        data.startedQuests.forEach(s -> {
            String[] arr = s.split(",");
            QuestLine line = ConfigManager.getLine(arr[0]);
            Quest quest = ConfigManager.getQuest(Integer.parseInt(arr[1]));
            text.add(Text.of(
                    TextColors.YELLOW, "* " + line.getName(),
                    TextColors.GRAY, "/",
                    TextColors.YELLOW, quest.displayName
            ));

            quest.tasks.forEach(t -> {
                BaseTask task = t.getValue();
                QuestStatus questStatus = data.getStatus(task, line, quest);
                text.add(Text.of(
                        TextColors.YELLOW, "  > ", task,
                        TextColors.GRAY, " | ",
                        task instanceof AmountTask ?
                                Text.of(
                                        TextColors.GREEN, questStatus.current, "/", ((AmountTask) task).getTotal(),
                                        TextColors.LIGHT_PURPLE, " (", ((AmountTask) task).getPercentageCompleted(questStatus), "%)") :
                                task.isCompleted(data, line, quest) ?
                                        Util.toText(ConfigManager.getConfig().messages.taskCompleted) :
                                        Util.toText(ConfigManager.getConfig().messages.taskNotCompleted)
                ));
            });
            text.add(Text.of());
        });

        if (text.isEmpty())
            text.add(Util.toText(ConfigManager.getConfig().messages.noQuestsStarted));
        else
            text.remove(text.size() - 1);

        PaginationList.builder()
                .padding(Text.of(TextColors.WHITE, TextStyles.STRIKETHROUGH, "-"))
                .title(Util.toText(ConfigManager.getConfig().messages.playerQuestInfo.replace("%player%", ((Player) src).getName())))
                .contents(text)
                .sendTo(src);
        return CommandResult.success();
    }
}
