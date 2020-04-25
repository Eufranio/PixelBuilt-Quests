package online.pixelbuilt.pbquests.storage.sql.persister;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import online.pixelbuilt.pbquests.task.TaskType;
import org.spongepowered.api.Sponge;

public class TaskTypePersister extends StringType {

    private static final TaskTypePersister INSTANCE = new TaskTypePersister();

    private TaskTypePersister() {
        super(SqlType.STRING, new Class<?>[] { TaskType.class });
    }

    public static TaskTypePersister getSingleton() {
        return INSTANCE;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        TaskType obj = (TaskType) javaObject;
        return obj != null ? obj.getId() : null;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ? Sponge.getRegistry().getType(TaskType.class, (String) sqlArg).orElse(null) : null;
    }

}
