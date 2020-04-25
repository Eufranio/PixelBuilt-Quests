package online.pixelbuilt.pbquests.commands;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Rename extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Only players can run this command!"));
        }
        Player p = (Player) src;

        Text name = args.requireOne("name");
        p.sendMessage(Text.of(
                TextColors.GREEN, "Click in the entity that you want to rename!"
        ));
        Sponge.getEventManager().registerListeners(PixelBuiltQuests.instance, new OneTimeHandler((e, player) -> {
            if (player.getUniqueId().equals(p.getUniqueId())) {
                e.getTargetEntity().offer(Keys.DISPLAY_NAME, name);
                player.sendMessage(Text.of(
                        TextColors.GREEN, "Successfully renamed entity!"
                ));
            }
        }));
        return CommandResult.success();
    }
}
