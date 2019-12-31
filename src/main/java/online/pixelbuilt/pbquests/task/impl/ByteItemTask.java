package online.pixelbuilt.pbquests.task.impl;

import de.randombyte.byteitems.api.ByteItemsService;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.config.ConfigManager;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.task.BaseTask;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

/**
 * Created by Frani on 31/12/2019.
 */
@ConfigSerializable
public class ByteItemTask implements BaseTask<ByteItemTask> {

    @Setting
    public String byteItemId = "byte-items:default";

    @Setting
    public int amount = 10;

    @Override
    public boolean check(Player player, Quest quest, QuestLine line, int questId) {
        ItemStack item = getItem(byteItemId).createStack();
        Inventory inv = player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item));
        if (inv.totalItems() < amount) {
            player.sendMessage(Util.toText(ConfigManager.getConfig().messages.noItem
                    .replace("%item%", item.getType().getName())
                    .replace("%amount%", ""+amount)
            ));
            return false;
        }
        return true;
    }

    @Override
    public void complete(Player player, Quest quest, QuestLine line, int questId) {
        ItemStack item = getItem(byteItemId).createStack();
        player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item)).poll(amount);
    }

    private ItemStackSnapshot getItem(String id) {
        return Sponge.getServiceManager().provideUnchecked(ByteItemsService.class)
                .get(id)
                .orElse(ItemTypes.GRASS.getTemplate());
    }

}
