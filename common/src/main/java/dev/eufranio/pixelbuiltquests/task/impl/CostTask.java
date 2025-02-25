package dev.eufranio.pixelbuiltquests.task.impl;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.config.ConfigManager;
import dev.eufranio.pixelbuiltquests.economy.QuestsEconomyProvider;
import dev.eufranio.pixelbuiltquests.storage.sql.TaskStatus;
import dev.eufranio.pixelbuiltquests.task.AmountTask;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.task.TaskTypes;
import dev.eufranio.pixelbuiltquests.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.UUID;

@ConfigSerializable
public class CostTask implements AmountTask {

    @Setting
    public String id = "0";

    @Setting
    private int cost = 0;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public TaskType getType() {
        return TaskTypes.COST;
    }

    @Override
    public int getTotal() {
        return this.cost;
    }

    @Override
    public void tryIncrease(UUID player, TaskStatus status) {
        try {
            QuestsEconomyProvider.get();
        } catch (IllegalStateException ex) {
            PixelBuiltQuests.instance().logger().error("PBQ needs an economy plugin if quest prices are enabled!");
            return;
        }

        if (QuestsEconomyProvider.get().has(player, this.cost)) {
            QuestsEconomyProvider.get().withdraw(player, this.cost);
            this.increase(player, status, this.cost);
            return;
        }

        ServerPlayer p = Util.player(player);
        if (p != null) {
            p.sendSystemMessage(Util.text(ConfigManager.getConfig().messages.noMoney
                    .replace("%money%", ""+cost)
            ));
        }
    }

    @Override
    public MutableComponent toText() {
        return Component.empty().withStyle(ChatFormatting.YELLOW)
                .append("Cost (")
                .append(Component.literal("$" + this.cost).withStyle(ChatFormatting.AQUA))
                .append(")");
    }
}
