package dev.eufranio.pixelbuiltquests.task.impl;

import dev.eufranio.pixelbuiltquests.storage.sql.TaskStatus;
import dev.eufranio.pixelbuiltquests.task.AmountTask;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.task.TaskTypes;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@ConfigSerializable
public class ItemTask implements AmountTask {

    @Setting
    public String id = "1";

    @Setting
    public String item = "minecraft:stone";

    @Setting
    public int amount = 1;

    @Override
    public TaskType getType() {
        return TaskTypes.ITEM;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getTotal() {
        return this.amount;
    }

    @Override
    public void tryIncrease(UUID player, TaskStatus status) {
        ServerPlayer serverPlayer = Util.player(player);

        Item thisItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(this.item))
                .orElse(Items.STONE);

        List<ItemStack> stacks = serverPlayer.getInventory().items.stream()
                .filter(stack -> stack.getItem() == thisItem)
                .toList();

        if (stacks.isEmpty())
            return;

        int current = status.getValue();
        int totalQuantity = stacks.stream().mapToInt(ItemStack::getCount).sum();
        int toRemove = Math.min(this.getTotal() - current, totalQuantity);
        int remaining = toRemove;

        Iterator<ItemStack> stack = stacks.iterator();
        while (stack.hasNext() && remaining > 0) {
            ItemStack next = stack.next();
            if (next.getCount() > remaining) {
                next.shrink(remaining);
                remaining = 0;
            } else {
                remaining -= next.getCount();
                next.setCount(0);
            }
        }

        increase(player, status, toRemove);
    }

    @Override
    public MutableComponent toText() {
        ItemStack item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(this.item))
                .orElse(Items.STONE)
                .getDefaultInstance();
        return Component.empty().withStyle(ChatFormatting.YELLOW)
                .append("Item (")
                .append(Component.empty().withStyle(ChatFormatting.AQUA)
                        .append(this.amount + "x ")
                        .append(item.getHoverName())
                        .withStyle(style -> style.withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(item))
                        ))
                )
                .append(")");
    }

}
