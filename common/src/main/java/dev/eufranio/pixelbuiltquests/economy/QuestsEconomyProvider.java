package dev.eufranio.pixelbuiltquests.economy;

import org.checkerframework.checker.nullness.qual.NonNull;

public class QuestsEconomyProvider {

    private static QuestsEconomy instance = null;

    public static @NonNull QuestsEconomy get() {
        QuestsEconomy instance = QuestsEconomyProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("QuestsEconomy has not been initialized yet");
        } else {
            return instance;
        }
    }

    public static void register(QuestsEconomy instance) {
        QuestsEconomyProvider.instance = instance;
    }


}
