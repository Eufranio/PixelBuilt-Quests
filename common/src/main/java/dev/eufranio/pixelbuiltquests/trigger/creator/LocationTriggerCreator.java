package dev.eufranio.pixelbuiltquests.trigger.creator;

import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.storage.sql.Trigger;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.concurrent.CompletableFuture;

public class LocationTriggerCreator implements TriggerCreator {

    @Override
    public CompletableFuture<Trigger> createNewTrigger(ServerPlayer player, TriggerType type, QuestReference quest, boolean cancelOriginalAction) {
        BlockPos pos = player.getOnPos();
        ResourceKey<Level> level = player.level().dimension();
        Trigger trigger = new Trigger(quest, pos, level, type, cancelOriginalAction);
        player.sendSystemMessage(Component.empty().withStyle(ChatFormatting.AQUA)
                .append("Successfully selected location ")
                .append(Util.locationToText(pos, (ServerLevel) player.level())));
        return CompletableFuture.completedFuture(trigger);
    }

}
