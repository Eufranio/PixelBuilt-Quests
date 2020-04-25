package online.pixelbuilt.pbquests.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Spawn extends BaseCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        BlockRay<World> blockRay = BlockRay.from((Player) src)
                .skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                .build();
        Optional<BlockRayHit<World>> hitOpt = blockRay.end();
        if(hitOpt.isPresent()) {
            EntityType entity = args.requireOne("type");

            Entity npc = hitOpt.get().getExtent().createEntity(entity, hitOpt.get().getPosition());
            npc.offer(Keys.PERSISTS, true);
            npc.offer(Keys.AI_ENABLED, false);
            npc.offer(Keys.CUSTOM_NAME_VISIBLE, true);
            npc.offer(Keys.INVULNERABILITY_TICKS, Integer.MAX_VALUE);
            npc.offer(Keys.INVULNERABLE, true);
            npc.offer(Keys.HAS_GRAVITY, false);
            hitOpt.get().getExtent().spawnEntity(npc);

            src.sendMessage(Text.of(
                    TextColors.GREEN, "Successfully spawned NPC! Assign a quest to it now!"
            ));
        } else {
            throw new CommandException(Text.of("You're not looking to a block!"));
        }

        return CommandResult.success();
    }
}