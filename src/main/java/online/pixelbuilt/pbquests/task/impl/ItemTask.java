package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.AmountTask;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class ItemTask implements AmountTask {

    @Setting
    public int id;

    @Setting
    public ItemType item = ItemTypes.STONE;

    @Setting
    public int amount = 1;

    @Override
    public TaskType getType() {
        return TaskTypes.ITEM;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getTotal() {
        return this.amount;
    }

    @Override
    public void tryIncrease(PlayerData data, QuestStatus status) {
        data.getUser().getPlayer().ifPresent(p -> {
            Inventory inv = p.getInventory().query(QueryOperationTypes.ITEM_TYPE.of(item));
            int toRemove = this.getTotal() - status.current;
            this.increase(data, status, inv.poll(toRemove).get().getQuantity());
        });
    }
}
