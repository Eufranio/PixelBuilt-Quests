package dev.eufranio.pixelbuiltquests.economy;

import java.util.UUID;

public interface QuestsEconomy {

    boolean has(UUID player, int value);

    void withdraw(UUID player, int value);

    void deposit(UUID player, int value);

}
