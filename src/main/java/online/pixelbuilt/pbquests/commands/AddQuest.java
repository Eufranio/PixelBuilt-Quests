package online.pixelbuilt.pbquests.commands;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.storage.sql.Trigger;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class AddQuest extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("You must be a player to run this command!"));
        }

        Player p = (Player) src;
        Quest quest = args.requireOne("quest");
        QuestLine line = args.requireOne("quest line");
        Trigger.Type type = args.requireOne("type");
        boolean cancelOriginalAction = args.<Boolean>getOne("cancel action").orElse(true);

        if (type == Trigger.Type.NPC) {
            src.sendMessage(Text.of(
                    TextColors.GRAY, "Right click the entity that you want to assign this quest to!"
            ));

            Sponge.getEventManager().registerListeners(PixelBuiltQuests.instance, new OneTimeHandler((e, player) -> {
                if (player.getUniqueId().equals(p.getUniqueId())) {
                    storageManager.addNPC(e.getTargetEntity(), line, quest, cancelOriginalAction);
                    src.sendMessage(Text.of(
                            TextColors.GREEN, "Successfully added NPC!"
                    ));
                }
            }));
        } else {
            Location<World> loc = p.getLocation().sub(0, 1, 0);
            Trigger trigger = new Trigger(loc, quest, line, type, null, cancelOriginalAction);
            storageManager.addTrigger(trigger);
            src.sendMessage(Text.of(
                    TextColors.GREEN, "Successfully added trigger at ", Util.locationToText(trigger.location)
            ));
        }

        return CommandResult.success();
    }
}
