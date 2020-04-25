package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.AmountTask;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class CostTask implements AmountTask {

    @Setting
    public int id = 0;

    @Setting
    private int cost = 0;

    @Override
    public int getId() {
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
    public void tryIncrease(PlayerData data, QuestStatus status) {
        EconomyService service = Sponge.getServiceManager().provide(EconomyService.class).orElse(null);
        if (service == null) {
            PixelBuiltQuests.getInstance().getLogger().error("PBQ needs an economy plugin if quest prices are enabled!");
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
        }
    }

    @Override
    public Text getDisplay() {
        return Text.of(TextColors.YELLOW, "Cost (", Text.of(TextColors.AQUA, "$" + this.cost), ")");
    }
}
