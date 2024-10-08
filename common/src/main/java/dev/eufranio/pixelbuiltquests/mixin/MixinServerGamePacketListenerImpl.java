package dev.eufranio.pixelbuiltquests.mixin;

import dev.architectury.event.EventResult;
import dev.eufranio.pixelbuiltquests.events.internal.PBQInternalEvents;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Set;

/// From [Sponge](https://github.com/SpongePowered/Sponge/blob/805e378b571aa2d2771897f94e67e1abb299d700/src/mixins/java/org/spongepowered/common/mixin/core/server/network/ServerGamePacketListenerImplMixin.java#L227)
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {

    @Shadow public ServerPlayer player;

    @Shadow public abstract void teleport(double d, double e, double f, float g, float h, Set<RelativeMovement> set);

    @Inject(
            method = "handleMovePlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isPassenger()Z"),
            cancellable = true
    )
    private void pixelbuiltquests$callMoveEvent(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        final boolean fireMoveEvent = packet.hasPosition();

        // During login, minecraft sends a packet containing neither the 'moving' or 'rotating' flag set - but only once.
        // We don't fire an event to avoid confusing plugins.
        if (!fireMoveEvent) {
            return;
        }

        final Vec3 fromPosition = player.position();

        final Vec3 originalToPosition = new Vec3(packet.getX(this.player.getX()),
                packet.getY(this.player.getY()), packet.getZ(this.player.getZ()));

        if (fromPosition.equals(originalToPosition))
            return;

        final Vec3 originalToRotation = new Vec3(packet.getXRot(this.player.getXRot()),
                packet.getYRot(this.player.getYRot()), 0);

        // common checks and throws are done here.
        final @Nullable Vec3 toPosition;
        if (fireMoveEvent) {
            EventResult result = PBQInternalEvents.MOVE_ENTITY.invoker().moveEntity(
                    player,
                    player.level().dimension(),
                    fromPosition,
                    originalToPosition
            );
            if (result.interruptsFurtherEvaluation()) {
                toPosition = null;
            } else {
                toPosition = originalToPosition;
            }
        } else {
            toPosition = originalToPosition;
        }

        // At this point, we cancel out and let the "confirmed teleport" code run through to update the
        // player position and update the player's relation in the chunk manager.
        if (toPosition == null) {
            // This will both cancel the movement and notify the client about the new rotation if any.
            // The position is absolute so the momentum will be reset by the client.
            // The rotation is relative so the head movement is still smooth.
            // The client thinks its current rotation is originalToRotation so the new rotation is relative to that.
            // The rotation values can be out of "valid" range so set them directly to the same value the client has.
            this.player.absMoveTo(fromPosition.x(), fromPosition.y(), fromPosition.z());
            //this.player.setXRot((float) originalToRotation.x());
            //this.player.setYRot((float) originalToRotation.y());
            this.teleport(fromPosition.x(), fromPosition.y(), fromPosition.z(),
                    (float) originalToRotation.y(), (float) originalToRotation.x(),
                    EnumSet.of(RelativeMovement.X_ROT, RelativeMovement.Y_ROT));
            ci.cancel();
            return;
        }
    }

    @Inject(
            method = "handleMoveVehicle",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getControllingPassenger()Lnet/minecraft/world/entity/LivingEntity;"),
            cancellable = true
    )
    private void pixelbuiltquests$handleVehicleMoveEvent(final ServerboundMoveVehiclePacket packet, final CallbackInfo ci) {
        //final ServerboundMoveVehiclePacketAccessor packet = (ServerboundMoveVehiclePacketAccessor) param0;
        final Entity rootVehicle = this.player.getRootVehicle();
        final Vec3 fromRotation = new Vec3(rootVehicle.getYRot(), rootVehicle.getXRot(), 0);

        // Use the position of the last movement with an event or the current player position if never called
        // We need this because we ignore very small position changes as to not spam as many move events.
        final Vec3 fromPosition = rootVehicle.position();

        final Vec3 originalToPosition = new Vec3(packet.getX(), packet.getY(), packet.getZ());
        final Vec3 originalToRotation = new Vec3(packet.getYRot(), packet.getXRot(), 0);

        // common checks and throws are done here.
        final @Nullable Vec3 toPosition;
        EventResult result = PBQInternalEvents.MOVE_ENTITY.invoker().moveEntity(
                player,
                player.level().dimension(),
                fromPosition,
                originalToPosition
        );
        if (result.interruptsFurtherEvaluation()) {
            toPosition = null;
        } else {
            toPosition = originalToPosition;
        }

        if (toPosition == null) {
            // no point doing all that processing, just account for a potential rotation change.
            if (!fromRotation.equals(originalToRotation)) {
                rootVehicle.absMoveTo(rootVehicle.getX(), rootVehicle.getY(), rootVehicle.getZ(), (float) originalToRotation.y(), (float) originalToRotation.x());
            }
            this.player.connection.send(new ClientboundMoveVehiclePacket(rootVehicle));
            ci.cancel();
            return;
        }
    }

}
