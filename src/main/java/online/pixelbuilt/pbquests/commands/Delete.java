package online.pixelbuilt.pbquests.commands;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.storage.sql.Trigger;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Delete extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("You must be a player to run this!"));
        }
        Player p = (Player) src;

        String type = args.requireOne("block/npc");
        if (type.equalsIgnoreCase("block")) {
            BlockRay<World> blockRay = BlockRay.from((Player) src)
                    .skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                    .build();
            Optional<BlockRayHit<World>> hitOpt = blockRay.end();
            if (hitOpt.isPresent()) {
                Trigger trigger = storageManager.getTriggerAt(hitOpt.get().getLocation());
                if (trigger == null) {
                    throw new CommandException(Text.of("Invalid trigger!"));
                }

                storageManager.removeTrigger(trigger);
                src.sendMessage(Text.of(
                        TextColors.GREEN, "Successfully removed trigger!"
                ));
            } else {
                throw new CommandException(Text.of("You're not looking to a block!"));
            }

        } else if (type.equalsIgnoreCase("npc")) {
            src.sendMessage(Text.of(
                    TextColors.GRAY, "Right click the entity the entity that you want to remove the trigger!"
            ));

            Sponge.getEventManager().registerListeners(PixelBuiltQuests.instance, new OneTimeHandler((e, player) -> {
                if (player.getUniqueId().equals(p.getUniqueId())) {
                    Trigger trigger = storageManager.getTrigger(e.getTargetEntity());
                    if (trigger == null) {
                        player.sendMessage(Text.of(
                                TextColors.GREEN, "There's no trigger associated with this NPC!"
                        ));
                    } else {
                        storageManager.removeNPC(e.getTargetEntity());
                        e.getTargetEntity().remove();
                        player.sendMessage(Text.of(
                                TextColors.GREEN, "Successfully removed quest from this entity!"
                        ));
                    }
                }
            }));
        } else {
            throw new CommandException(Text.of("Unknown option! Specify block or npc!"));
        }

        return CommandResult.success();
    }
}