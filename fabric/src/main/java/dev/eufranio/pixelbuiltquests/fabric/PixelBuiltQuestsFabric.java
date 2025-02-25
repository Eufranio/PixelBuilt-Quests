package dev.eufranio.pixelbuiltquests.fabric;

import dev.eufranio.pixelbuiltquests.economy.QuestsEconomyProvider;
import dev.eufranio.pixelbuiltquests.fabric.economy.BlanketEconomyImpl;
import net.fabricmc.api.ModInitializer;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;

public final class PixelBuiltQuestsFabric implements ModInitializer {

    public PixelBuiltQuestsFabric() {
        QuestsEconomyProvider.register(new BlanketEconomyImpl());
        new PixelBuiltQuests();
    }

    @Override
    public void onInitialize() {

    }

}
