package dev.eufranio.pixelbuiltquests.fabric;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.economy.QuestsEconomyProvider;
import dev.eufranio.pixelbuiltquests.fabric.economy.BlanketEconomyImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class PixelBuiltQuestsFabric implements ModInitializer {

    public PixelBuiltQuestsFabric() {
        if (FabricLoader.getInstance().isModLoaded("blanketeconomy")) {
            QuestsEconomyProvider.register(new BlanketEconomyImpl());
        }
        new PixelBuiltQuests();
    }

    @Override
    public void onInitialize() {

    }

}
