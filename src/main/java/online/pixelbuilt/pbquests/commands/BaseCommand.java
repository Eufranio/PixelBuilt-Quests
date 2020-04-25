package online.pixelbuilt.pbquests.commands;

import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.storage.StorageManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

import java.util.function.BiConsumer;

public abstract class BaseCommand implements CommandExecutor {

    PixelBuiltQuests plugin = PixelBuiltQuests.getInstance();
    StorageManager storageManager = plugin.getStorage();

    public static class OneTimeHandler {

        private BiConsumer<InteractEntityEvent, Player> func;
        OneTimeHandler(BiConsumer<InteractEntityEvent, Player> function) {
            this.func = function;
        }

        @Listener(beforeModifications = true, order = Order.FIRST)
        public void onRightClick(InteractEntityEvent.Secondary.MainHand event, @Root Player player) {
            this.func.accept(event, player);
            event.setCancelled(true);
            Sponge.getEventManager().unregisterListeners(this);
        }

    }

}
