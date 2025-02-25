package dev.eufranio.pixelbuiltquests.fabric.economy;

import dev.eufranio.pixelbuiltquests.economy.QuestsEconomy;
import org.blanketeconomy.api.BlanketEconomy;

import java.math.BigDecimal;
import java.util.UUID;

public class BlanketEconomyImpl implements QuestsEconomy {

    @Override
    public boolean has(UUID player, int value) {
        return BlanketEconomy.INSTANCE.getAPI()
                .hasEnoughFunds(player, new BigDecimal(value), getCurrencyName());
    }

    @Override
    public void withdraw(UUID player, int value) {
        BlanketEconomy.INSTANCE.getAPI()
                .subtractBalance(player, new BigDecimal(value), getCurrencyName());
    }

    @Override
    public void deposit(UUID player, int value) {
        BlanketEconomy.INSTANCE.getAPI()
                .addBalance(player, new BigDecimal(value), getCurrencyName());
    }

    String getCurrencyName() {
        return BlanketEconomy.INSTANCE.getAPI()
                .getPrimaryCurrency()
                .getCurrencyType();
    }

}
