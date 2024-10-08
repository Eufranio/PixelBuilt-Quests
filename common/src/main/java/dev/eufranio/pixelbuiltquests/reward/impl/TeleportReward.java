package dev.eufranio.pixelbuiltquests.reward.impl;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.reward.BaseReward;
import dev.eufranio.pixelbuiltquests.reward.RewardType;
import dev.eufranio.pixelbuiltquests.reward.RewardTypes;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.UUID;

@ConfigSerializable
public class TeleportReward implements BaseReward {

    @Override
    public RewardType getType() {
        return RewardTypes.TELEPORT;
    }

    @Setting
    private GlobalPos location = GlobalPos.of(Level.OVERWORLD, new BlockPos(0, 0, 0));

    @Override
    public void execute(UUID player, QuestReference reference) {
        ServerPlayer serverPlayer = Util.player(player);
        serverPlayer.teleportTo(
                PixelBuiltQuests.server().getLevel(location.dimension()),
                location.pos().getX(),
                location.pos().getY(),
                location.pos().getZ(),
                serverPlayer.getYRot(),
                serverPlayer.getXRot()
        );
    }

}
