package online.pixelbuilt.pbquests.task.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.PixelBuiltQuests;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.storage.StorageManager;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import online.pixelbuilt.pbquests.storage.sql.QuestStatus;
import online.pixelbuilt.pbquests.task.TaskType;
import online.pixelbuilt.pbquests.task.TaskTypes;
import online.pixelbuilt.pbquests.task.TriggeredTask;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 28/01/2019.
 */
@ConfigSerializable
public class KillTask implements TriggeredTask<DestructEntityEvent.Death> {

    @Setting
    public int id = 2;

    @Setting(comment = "checking mode. 1 = killed at least <count> specific entities, " +
            "2 = killed at least <count> entities")
    public int checkMode = 1;

    @Setting
    public int count = 5;

    @Setting
    public EntityType mob = EntityTypes.ZOMBIE;

    @Override
    public TaskType getType() {
        return TaskTypes.KILL;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getTotal() {
        return this.count;
    }

    @Override
    public Class<DestructEntityEvent.Death> getEventClass() {
        return DestructEntityEvent.Death.class;
    }

    @Override
    public Text getDisplay() {
        return Text.of(TextColors.YELLOW, "Kill (", Text.of(TextColors.AQUA, this.count, " ", checkMode == 2 ? "Entities" : mob.getName()), ")");
    }

    @Override
    public void handle(QuestLine line, Quest quest, DestructEntityEvent.Death event) {
        if (event.getCause().root() instanceof EntityDamageSource) {
            EntityDamageSource root = (EntityDamageSource) event.getCause().root();
            if (root.getSource() instanceof Player) {
                if ((checkMode == 1 && event.getTargetEntity().getType() == this.mob) || checkMode == 2) {
                    PlayerData data = ((StorageManager) PixelBuiltQuests.getStorage()).getData(root.getSource().getUniqueId());
                    QuestStatus status = data.getStatus(this, line, quest);
                    this.increase(data, status, 1);
                }
            }
        }
    }
}
