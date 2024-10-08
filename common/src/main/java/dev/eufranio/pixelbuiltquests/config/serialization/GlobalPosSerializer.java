package dev.eufranio.pixelbuiltquests.config.serialization;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class GlobalPosSerializer implements TypeSerializer<GlobalPos> {

    @Override
    public GlobalPos deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String worldId = node.node("world").getString();
        ResourceKey<Level> world = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(worldId));
        int x = node.node("x").getInt();
        int y = node.node("y").getInt();
        int z = node.node("z").getInt();
        BlockPos pos = new BlockPos(x, y, z);
        return GlobalPos.of(world, pos);
    }

    @Override
    public void serialize(Type type, @Nullable GlobalPos obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            return;
        }

        node.node("world").set(obj.dimension().location().toString());
        node.node("x").set(obj.pos().getX());
        node.node("y").set(obj.pos().getY());
        node.node("z").set(obj.pos().getZ());
    }

}
