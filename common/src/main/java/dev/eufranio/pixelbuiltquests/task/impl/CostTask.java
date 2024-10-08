package dev.eufranio.pixelbuiltquests.task.impl;

import dev.eufranio.pixelbuiltquests.storage.sql.TaskStatus;
import dev.eufranio.pixelbuiltquests.task.AmountTask;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.task.TaskTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
        /*EconomyService service = Sponge.getServiceManager().provide(EconomyService.class).orElse(null);
        if (service == null) {
            PixelBuiltQuests.instance().logger().error("PBQ needs an economy plugin if quest prices are enabled!");
            data.getUser().getPlayer().ifPresent(p ->
                    p.sendMessage(Text.of(
                            TextColors.RED, "An error ocurred while checking the requeriments of this quest, contact an staff!")
                    )
            );
            return;
        }

        UniqueAccount account = service.getOrCreateAccount(data.id).get();
        BigDecimal cost = new BigDecimal(this.cost);
        TransactionResult result = account.withdraw(service.getDefaultCurrency(), cost, Sponge.getCauseStackManager().getCurrentCause());
        if (result.getResult() != ResultType.SUCCESS) {
            data.getUser().getPlayer().ifPresent(p ->
                    p.sendMessage(Util.toText(ConfigManager.getConfig().messages.noMoney
                            .replace("%money%", cost.toString())
                    ))
            );
            return;
        } else {
            this.increase(data, status, this.cost);
        }*/
    }

    @Override
    public MutableComponent toText() {
        return Component.empty().withStyle(ChatFormatting.YELLOW)
                .append("Cost (")
                .append(Component.literal("$" + this.cost).withStyle(ChatFormatting.AQUA))
                .append(")");
    }
}
