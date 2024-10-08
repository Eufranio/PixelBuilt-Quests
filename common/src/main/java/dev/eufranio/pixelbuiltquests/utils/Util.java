package dev.eufranio.pixelbuiltquests.utils;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.chat.ChatType;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Util {

    public static boolean hasPermission(CommandSourceStack source, String permission) {
        if (!source.isPlayer()) {
            return true;
        }
        return hasPermission(source.getPlayer().getUUID(), permission);
    }

    public static boolean hasPermission(Player player, String permission) {
        return hasPermission(player.getUUID(), permission);
    }

    public static boolean hasPermission(UUID player, String permission) {
        User user = LuckPermsProvider.get().getUserManager().getUser(player);
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public static void sendChatTypeMessage(ServerPlayer player, ChatType type, Component message) {
        if (type == ChatType.CHAT) {
            player.sendSystemMessage(message);
            return;
        }

        if (type == ChatType.ACTION_BAR) {
            player.connection.send(new ClientboundSetActionBarTextPacket(message));
            return;
        }
    }

    public static BlockPos blockPos(Vec3 pos) {
        return new BlockPos(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z));
    }

    public static ServerPlayer player(UUID uuid) {
        return PixelBuiltQuests.server().getPlayerList().getPlayer(uuid);
    }

    public static Item item(String id, Item... def) {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(id))
                .orElse(def.length > 0 ? def[0] : null);
    }

    public static MutableComponent text(String string) {
        return Component.literal(string.replace("&", "§"));
    }

    public static ServerLevel level(ResourceKey<Level> key) {
        return PixelBuiltQuests.server().getLevel(key);
    }

    public static MutableComponent locationToText(GlobalPos pos) {
        return locationToText(pos.pos(), level(pos.dimension()));
    }

    public static MutableComponent locationToText(BlockPos pos, @Nullable ServerLevel world) {
        return Component.empty().withStyle(ChatFormatting.YELLOW)
                .append("world=" + (world == null ? "invalid" : ((ServerLevelData) world.getLevelData()).getLevelName()))
                .append(" x=" + pos.getX())
                .append(" y=" + pos.getY())
                .append(" z=" + pos.getZ());
    }

    public static String timeDiffFormat(long timeDiffSeconds, boolean includeSeconds) {
        String timeFormat;
        int seconds = (int) timeDiffSeconds % 60;
        timeDiffSeconds = timeDiffSeconds / 60;
        int minutes = (int) timeDiffSeconds % 60;
        timeDiffSeconds = timeDiffSeconds / 60;
        int hours = (int) timeDiffSeconds % 24;
        timeDiffSeconds = timeDiffSeconds / 24;
        int days = (int) timeDiffSeconds;

        if (days > 7) {
            timeFormat = days + " days";
        } else if (days > 0) {
            timeFormat = days + "d " + hours + "h";
        } else if (days == 0 && hours > 0) {
            if (includeSeconds) {
                timeFormat = hours + "h " + minutes + "m " + seconds + "s";
            } else {
                timeFormat = hours + "h " + minutes + "m";
            }
        } else if (days == 0 && hours == 0 && minutes > 0) {
            if (includeSeconds) {
                timeFormat = minutes + "m " + seconds + "s";
            } else {
                timeFormat = minutes + "m";
            }
        } else {
            timeFormat = seconds + "s";
        }

        return timeFormat;
    }

}
