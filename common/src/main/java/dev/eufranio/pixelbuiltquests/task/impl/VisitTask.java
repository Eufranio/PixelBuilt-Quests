package dev.eufranio.pixelbuiltquests.task.impl;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.task.QuestTaskReference;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.task.TaskTypes;
import dev.eufranio.pixelbuiltquests.task.TriggeredTask;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class VisitTask implements TriggeredTask {

    @Setting
    private String id = "5";

    @Setting
    private GlobalPos visitLocation = GlobalPos.of(Level.OVERWORLD, new BlockPos(0, 0, 0));

    @Setting
    private int visitRadius = 5;

    @Override
    public TaskType getType() {
        return TaskTypes.VISIT;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getTotal() {
        return 1;
    }

    @Override
    public MutableComponent toText() {
        MutableComponent location = Util.locationToText(this.visitLocation);
        return Component.empty().withStyle(ChatFormatting.YELLOW)
                .append("Visit (")
                .append(location.withStyle(ChatFormatting.AQUA))
                .append(")");
    }

    @Override
    public void handle(QuestReference quest, Object... data) {
        Entity entity = (Entity) data[0];
        ResourceKey<Level> world = (ResourceKey<Level>) data[1];
        Vec3 from = (Vec3) data[2];
        Vec3 to = (Vec3) data[3];

        if (!(entity instanceof Player))
            return;

        BlockPos pos = new BlockPos(Mth.floor(to.x), Mth.floor(to.y), Mth.floor(to.z));

        if (pos.distManhattan(this.visitLocation.pos()) <= this.visitRadius) {
            ServerPlayer player = (ServerPlayer) entity;
            if (!world.equals(this.visitLocation.dimension()))
                return;

            if (!this.isCompleted(player.getUUID(), quest)) {
                PixelBuiltQuests.storage().getStatus(player.getUUID(), QuestTaskReference.of(quest, this))
                        .ifPresent(status -> this.increase(player.getUUID(), status, 1));
            }
        }
    }

}
