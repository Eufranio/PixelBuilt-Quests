package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class ItemTask implements BaseTask<ItemTask> {

    @Setting
    public ItemType item = ItemTypes.STONE;

    @Setting
    public int amount = 1;

    @Override
    public boolean check(Player player, Quest quest, QuestLine line, int questId) {
        Inventory inv = player.getInventory().query(QueryOperationTypes.ITEM_TYPE.of(item));
        if (inv.totalItems() < amount) {
            player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noItem
                    .replace("%item%", item.getName())
                    .replace("%amount%", ""+amount)
            ));
            return false;
        }
        return true;
    }

    @Override
    public void complete(Player player, Quest quest, QuestLine line, int questId) {
        player.getInventory().query(QueryOperationTypes.ITEM_TYPE.of(item)).poll(amount);
    }
}
