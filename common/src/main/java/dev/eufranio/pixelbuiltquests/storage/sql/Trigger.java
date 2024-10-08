package dev.eufranio.pixelbuiltquests.storage.sql;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.storage.sql.persister.BlockPosPersister;
import dev.eufranio.pixelbuiltquests.storage.sql.persister.TriggerTypePersister;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@DatabaseTable(tableName = "triggers")
public class Trigger extends BaseDaoEnabled<Trigger, Integer> {

    // Empty constructor for ORMLite
    public Trigger() {}

    // Entity triggers
    public Trigger(QuestReference quest,
                   UUID entity,
                   BlockPos entityPosition,
                   ResourceKey<Level> world,
                   TriggerType triggerType,
                   boolean cancelOriginalAction) {
        this.line = quest.getQuestLine().getId();
        this.questId = quest.getQuest().getId();
        this.entity = entity;
        this.pos = entityPosition;
        this.world = world.location().toString();
        this.type = triggerType;
        this.cancelOriginalAction = cancelOriginalAction;
        setDao(PixelBuiltQuests.triggerManager().triggerDao().objDao);
    }

    // Block triggers
    public Trigger(QuestReference quest,
                   BlockPos pos,
                   ResourceKey<Level> world,
                   TriggerType triggerType,
                   boolean cancelOriginalAction) {
        this.line = quest.getQuestLine().getId();
        this.questId = quest.getQuest().getId();
        this.pos = pos;
        this.world = world.location().toString();
        this.type = triggerType;
        this.cancelOriginalAction = cancelOriginalAction;
        setDao(PixelBuiltQuests.triggerManager().triggerDao().objDao);
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String line;

    @DatabaseField(canBeNull = false)
    private String questId;

    @DatabaseField(persisterClass = BlockPosPersister.class, canBeNull = false)
    private BlockPos pos;

    @DatabaseField(canBeNull = false)
    private String world;

    @DatabaseField(canBeNull = false, persisterClass = TriggerTypePersister.class)
    private TriggerType type;

    @DatabaseField
    private UUID entity;

    @DatabaseField(canBeNull = false)
    private boolean cancelOriginalAction = true;

    @DatabaseField
    private String extra;

    public ResourceKey<Level> getWorld() {
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(world));
    }

    public UUID getEntity() {
        return entity;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getId() {
        return id;
    }

    public TriggerType getType() {
        return type;
    }

    public boolean shouldCancelOriginalAction() {
        return cancelOriginalAction;
    }

    public <T> @Nullable T extra(Class<T> clazz) {
        return new Gson().fromJson(this.extra, clazz);
    }

    public <T> void saveExtra(T value) {
        this.extra = new Gson().toJson(value);
    }

    public QuestReference getQuest() {
        return QuestReference.of(this.line, this.questId);
    }

}
