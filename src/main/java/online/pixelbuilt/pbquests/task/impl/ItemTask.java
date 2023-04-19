package online.pixelbuilt.pbquests.task.impl;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.AmountTask;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import online.pixelbuilt.pbquests.task.TriggeredTask;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class ItemTask implements TriggeredTask<ChangeInventoryEvent.Pickup.Pre> {

    @Setting
    public int id = 1;

    @Setting
    public ItemType item = ItemTypes.STONE;

    @Setting
    public int amount = 1;

    @Override
    public TaskType getType() {
        return TaskTypes.ITEM;
    }

    @Override
    public Class<ChangeInventoryEvent.Pickup.Pre> getEventClass() {
        return ChangeInventoryEvent.Pickup.Pre.class;
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
    public Text toText() {
        return Text.of(TextColors.YELLOW, "Item (",
                Text.of(TextColors.AQUA, amount, "x ", TextActions.showItem(item.getTemplate()), item.getTranslation()),
                ")"
        );
    }

    @Override
    public void tryIncrease(PlayerData data, QuestStatus status) {
        data.getUser().getPlayer().ifPresent(p -> {
            Inventory inv = p.getInventory().query(QueryOperationTypes.ITEM_TYPE.of(item));
            int toRemove = this.getTotal() - status.current;
            if (inv.peek(toRemove).isPresent()) {
                int quantity = inv.poll().get().getQuantity();
                this.increase(data, status, quantity);
            }
        });
    }

    @Override
    public void handle(QuestLine line, Quest quest, ChangeInventoryEvent.Pickup.Pre event) {
        if (event.isCancelled() || !quest.autocomplete)
            return;

        if (event.getOriginalStack().getType() != item)
            return;

        PlayerData data = PixelBuiltQuests.getStorage().getData(((Player) event.getSource()).getUniqueId());
        if (!this.isCompleted(data, line, quest)) {
            data.getStatus(this, line, quest)
                    .ifPresent(status -> {
                        if (!this.isCompleted(data, line, quest)) {
                            int maxToRemove = this.getTotal() - status.current;
                            int quantity = event.getOriginalStack().getQuantity();
                            int toRemove = Math.min(quantity, maxToRemove);

                            if ((quantity - toRemove) <= 0) {
                                event.setCustom(Lists.newArrayList());
                            } else {
                                ItemStackSnapshot newSnapshot = ItemStack.builder()
                                        .fromSnapshot(event.getOriginalStack())
                                        .quantity(quantity - toRemove)
                                        .build()
                                        .createSnapshot();
                                event.setCustom(Lists.newArrayList(newSnapshot));
                            }

                            this.increase(data, status, toRemove);
                        }
                    });
        }
    }
}
