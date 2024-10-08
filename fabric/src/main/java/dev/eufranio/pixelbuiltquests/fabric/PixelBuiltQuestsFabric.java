package dev.eufranio.pixelbuiltquests.fabric;

import net.fabricmc.api.ModInitializer;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;

public final class PixelBuiltQuestsFabric implements ModInitializer {

    public PixelBuiltQuestsFabric() {
        new PixelBuiltQuests();
    }

    @Override
    public void onInitialize() {}

}
