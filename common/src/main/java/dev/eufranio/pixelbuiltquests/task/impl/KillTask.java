package dev.eufranio.pixelbuiltquests.task.impl;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.task.QuestTaskReference;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.task.TaskTypes;
import dev.eufranio.pixelbuiltquests.task.TriggeredTask;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class KillTask implements TriggeredTask {

    @Setting
    private String id = "kill cow";

    @Setting
    @Comment("checking mode. SPECIFIC_ENTITY = killed at least <count> specific entities, " +
            "ANY_ENTITY = killed at least <count> entities")
    private CheckMode checkMode = CheckMode.SPECIFIC_ENTITY;

    @Setting
    private int count = 5;

    @Setting
    private String entityType = EntityType.ZOMBIE.arch$registryName().toString();

    @Override
    public TaskType getType() {
        return TaskTypes.KILL;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getTotal() {
        return this.count;
    }

    @Override

    public MutableComponent toText() {
        return Component.empty().withStyle(ChatFormatting.YELLOW)
                .append("Kill (")
                .append(Component.empty().withStyle(ChatFormatting.AQUA)
                        .append(String.valueOf(this.count))
                        .append(" ")
                        .append(this.checkMode == CheckMode.SPECIFIC_ENTITY ?
                                EntityType.byString(this.entityType).map(EntityType::getDescription).orElse(Component.literal("Invalid")) :
                                Component.literal("Entities")))
                .append(")");
    }

    @Override
    public void handle(QuestReference quest, Object... data) {
        Entity dead = (Entity) data[0];
        if (checkMode == CheckMode.SPECIFIC_ENTITY) {
            if (dead.getType() != EntityType.byString(this.entityType).orElse(null)) {
                return;
            }
        }

        // checkMode = any, or specific entity met
        DamageSource source = (DamageSource) data[1];
        ServerPlayer playerSource;

        if (source.getEntity() instanceof Player) {
            playerSource = (ServerPlayer) source.getEntity();
        } else if (source.getDirectEntity() instanceof Player) {
            playerSource = (ServerPlayer) source.getDirectEntity();
        } else {
            return;
        }

        if (!this.isCompleted(playerSource.getUUID(), quest)) {
            PixelBuiltQuests.storage().getStatus(playerSource.getUUID(), QuestTaskReference.of(quest, this))
                    .ifPresent(status -> this.increase(playerSource.getUUID(), status, 1));
        }
    }

    enum CheckMode {
        SPECIFIC_ENTITY,
        ANY_ENTITY
    }

}
