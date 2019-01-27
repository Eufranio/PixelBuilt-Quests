package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class CostTask implements BaseTask {

    @Setting
    private int defaultCost = 0;

    @Override
    public boolean complete(Map<String, String> options, Player player, Quest quest, QuestLine line, int questId) {
        if (options.getOrDefault("cost", "0").equals("0")) return true;

        EconomyService service = Sponge.getServiceManager().provide(EconomyService.class).orElse(null);
        if (service == null) {
            PixelBuiltQuests.getInstance().getLogger().error("PBQ needs an economy plugin if quest prices are enabled!");
            player.sendMessage(Text.of(
                    TextColors.RED, "An error ocurred while checking the requeriments of this quest, contact an staff!")
            );
            return false;
        }

        UniqueAccount account = service.getOrCreateAccount(player.getUniqueId()).orElse(null);
        BigDecimal cost = new BigDecimal(options.getOrDefault("cost", ""+this.defaultCost));
        if (cost.equals(BigDecimal.ZERO)) {
            return true;
        }

        TransactionResult result = account.withdraw(service.getDefaultCurrency(), cost, Sponge.getCauseStackManager().getCurrentCause());
        if (result.getResult() != ResultType.SUCCESS) {
            player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noMoney
                    .replace("%money%", cost.toString())
            ));
            return false;
        }

        return true;
    }

}
