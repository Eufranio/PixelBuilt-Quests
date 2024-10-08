package dev.eufranio.pixelbuiltquests.storage.sql.persister;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
import dev.eufranio.pixelbuiltquests.task.TaskType;
import dev.eufranio.pixelbuiltquests.trigger.TriggerType;

public class TriggerTypePersister extends StringType {

    private static final TriggerTypePersister INSTANCE = new TriggerTypePersister();

    private TriggerTypePersister() {
        super(SqlType.STRING, new Class<?>[] { TriggerType.class });
    }

    public static TriggerTypePersister getSingleton() {
        return INSTANCE;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        TriggerType obj = (TriggerType) javaObject;
        return obj != null ? obj.getId() : null;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ?
                PixelBuiltQuests.registry().get(TriggerType.class, (String) sqlArg) :
                null;
    }

}
