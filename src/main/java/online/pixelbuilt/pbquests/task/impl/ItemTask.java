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
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.translation.locale.Locales;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class ItemTask implements BaseTask {

    @Setting
    public ItemType defaultItem = ItemTypes.STONE;

    @Setting
    public int defaultAmount = 1;

    @Override
    public boolean complete(Map<String, String> options, Player player, Quest quest, QuestLine line, int questId) {
        String itemType = options.getOrDefault("item", defaultItem.getId());
        int amount = Integer.parseInt(options.getOrDefault("amount", ""+defaultAmount));

        ItemType type = Sponge.getRegistry().getType(ItemType.class, itemType).orElse(defaultItem);
        Inventory inv = player.getInventory().query(QueryOperationTypes.ITEM_TYPE.of(type));
        if (inv.totalItems() < amount) {
            player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noItem
                    .replace("%item%", type.getName())
                    .replace("%amount%", ""+amount)
            ));
            return false;
        } else {
            inv.poll(amount);
        }

        return true;
    }
}
