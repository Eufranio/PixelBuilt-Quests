package dev.eufranio.pixelbuiltquests.storage.sql.persister;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import net.minecraft.core.BlockPos;

public class BlockPosPersister extends StringType {

    private static final BlockPosPersister INSTANCE = new BlockPosPersister();

    private BlockPosPersister() {
        super(SqlType.STRING, new Class<?>[] { BlockPos.class });
    }

    public static BlockPosPersister getSingleton() {
        return INSTANCE;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        BlockPos obj = (BlockPos) javaObject;
        if (obj == null)
            return null;
        return obj.getX() + "," + obj.getY() + "," + obj.getZ();
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        if (sqlArg == null)
            return null;
        String[] array = sqlArg.toString().split(",");
        return new BlockPos(Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2]));
    }

}