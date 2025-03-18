package dev.eufranio.pixelbuiltquests.fabric;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.economy.QuestsEconomyProvider;
import dev.eufranio.pixelbuiltquests.fabric.economy.BEconomyImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

public final class PixelBuiltQuestsFabric implements ModInitializer {

    public PixelBuiltQuestsFabric() {
        if (FabricLoader.getInstance().isModLoaded("beconomy")) {
            LoggerFactory.getLogger(PixelBuiltQuests.MOD_ID)
                    .info("Registering PBQ Economy Integration with BEconomy");
            QuestsEconomyProvider.register(new BEconomyImpl());
        }
        new PixelBuiltQuests();
    }

    @Override
    public void onInitialize() {

    }

}
