package dev.eufranio.pixelbuiltquests.fabric.economy;

import dev.eufranio.pixelbuiltquests.economy.QuestsEconomy;
import org.beconomy.api.BEconomy;

import java.math.BigDecimal;
import java.util.UUID;

public class BEconomyImpl implements QuestsEconomy {

    @Override
    public boolean has(UUID player, int value) {
        return BEconomy.INSTANCE.getAPI()
                .hasEnoughFunds(player, new BigDecimal(value), getCurrencyName());
    }

    @Override
    public void withdraw(UUID player, int value) {
        BEconomy.INSTANCE.getAPI()
                .subtractBalance(player, new BigDecimal(value), getCurrencyName());
    }

    @Override
    public void deposit(UUID player, int value) {
        BEconomy.INSTANCE.getAPI()
                .addBalance(player, new BigDecimal(value), getCurrencyName());
    }

    String getCurrencyName() {
        return BEconomy.INSTANCE.getAPI()
                .getPrimaryCurrency()
                .getCurrencyType();
    }

}
