package dev.eufranio.pixelbuiltquests.events.internal;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface PBQInternalEvents {

    Event<MoveEntity> MOVE_ENTITY = EventFactory.createEventResult();

    @FunctionalInterface
    interface MoveEntity {

        EventResult moveEntity(Entity entity, ResourceKey<Level> world, Vec3 from, Vec3 to);

    }

}
