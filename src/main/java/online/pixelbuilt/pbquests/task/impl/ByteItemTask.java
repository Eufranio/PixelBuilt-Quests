package online.pixelbuilt.pbquests.task.impl;

import de.randombyte.byteitems.api.ByteItemsService;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.AmountTask;
import online.pixelbuilt.pbquests.task.TaskType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.translation.Translation;

/**
 * Created by Frani on 31/12/2019.
 */
@ConfigSerializable
public class ByteItemTask implements AmountTask {

    public static TaskType TASK_TYPE = new TaskType("byteitem", "ByteItem", ByteItemTask.class);

    @Setting
    public int id = 6;

    @Setting
    public String byteItemId = "byte-items:default";

    @Setting
    public int amount = 10;

    @Override
    public TaskType getType() {
        return TASK_TYPE;
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
        ItemStackSnapshot item = getItem(byteItemId);
        return Text.of(TextColors.YELLOW, "ByteItem ",
                "(",
                TextColors.AQUA, TextActions.showItem(item), item.get(Keys.DISPLAY_NAME).orElse(Text.of(item.getTranslation())),
                TextColors.YELLOW,
                ")"
        );
    }

    @Override
    public void tryIncrease(PlayerData data, QuestStatus status) {
        ItemStack item = getItem(byteItemId).createStack();
        data.getUser().getPlayer().ifPresent(p -> {
            Inventory inv = p.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(item));
            int toRemove = this.getTotal() - status.current;
            this.increase(data, status, inv.poll(toRemove).get().getQuantity());
        });
    }

    private ItemStackSnapshot getItem(String id) {
        return Sponge.getServiceManager().provideUnchecked(ByteItemsService.class)
                .get(id)
                .orElse(ItemTypes.GRASS.getTemplate());
    }

}
