package online.pixelbuilt.pbquests.utils;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.persistence.QuestPersistenceService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by Frani on 06/09/2017.
 */
public class Command {

    public static void registerCommand() {
        CommandSpec spec = CommandSpec.builder()
                .description(Text.of("Main command of the PixelBuilt-Quests plugin"))
                .permission("pbquests.admin")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("action"))),
                        GenericArguments.remainingJoinedStrings(Text.of("args")))
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        if (args.<String>getOne("action").isPresent() && args.<String>getOne("args").isPresent()) {
                            String action = args.<String>getOne("action").get();
                            String arg = args.<String>getOne("args").get();

                            BlockRay<World> blockRay = BlockRay.from((Player)src)
                                    .skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                                    .build();
                            Optional<BlockRayHit<World>> hitOpt = blockRay.end();
                            if(hitOpt.isPresent()) {

                                // Spawning the NPC
                                if(action.equalsIgnoreCase("spawnnpc")) {

                                    EntityType entity = EntityTypes.VILLAGER;

                                    if (Sponge.getRegistry().getType(EntityType.class, arg).isPresent()) {
                                        entity = Sponge.getRegistry().getType(EntityType.class, arg).get();
                                    }

                                    Living npc = (Living) hitOpt.get().getExtent()
                                            .createEntity(entity, hitOpt.get().getPosition());
                                    npc.offer(Keys.PERSISTS, true);
                                    npc.offer(Keys.AI_ENABLED, false);
                                    npc.offer(Keys.CUSTOM_NAME_VISIBLE, true);
                                    npc.offer(Keys.WALKING_SPEED, 0D);
                                    npc.offer(Keys.INFINITE_DESPAWN_DELAY, true);
                                    npc.offer(Keys.INVULNERABILITY_TICKS, Integer.MAX_VALUE);
                                    npc.offer(Keys.HAS_GRAVITY, false);

                                    hitOpt.get().getExtent().spawnEntity(npc);

                                } else if (action.equalsIgnoreCase("checkprogress")) {
                                    ChatUtils.waitForResponse((Player)src,"&a Type the Quest Line that you want to query", (player1, questLine) -> {
                                        Player player = Sponge.getServer().getPlayer(arg).get();
                                        Sponge.getServiceManager()
                                                .provide(QuestPersistenceService.class)
                                                .ifPresent(questPersistenceService ->
                                                        questPersistenceService.getProgress(player.getUniqueId(), questLine, i -> {
                                                            player1.sendMessage(
                                                                    TextSerializers.FORMATTING_CODE
                                                                            .deserialize("&a Progress of &e" + arg + ": " + i
                                                                        ));
                                                }));
                                        });
                                } else if (action.equalsIgnoreCase("setprogress")) {
                                    ChatUtils.waitForResponse((Player)src, "&a Type the Quest Line that you want to set the progress of the player", (player, questLine) -> {
                                        ChatUtils.waitForResponse((Player)src, "&aType the progress that you want to set for " + arg, (player1, progress) -> {
                                            Sponge.getServiceManager()
                                                    .provide(QuestPersistenceService.class)
                                                    .ifPresent(questPersistenceService ->
                                                            questPersistenceService.setProgressLevel(Sponge.getServer().getPlayer(arg).get(),
                                                            questLine,
                                                            Integer.parseInt(progress),
                                                            () -> player.sendMessage(
                                                                    TextSerializers.FORMATTING_CODE.deserialize("&a Sucessfully updated the progress level of &e" + arg + "&a to &e" + progress + "&a!")
                                                            )));
                                                    });
                                            });
                                }

                            }
                        }

                        return CommandResult.success();
                    }
                })
                .build();
        Sponge.getCommandManager().register(PixelBuiltQuests.instance, spec, "pbq", "pbquests");
    }

}
