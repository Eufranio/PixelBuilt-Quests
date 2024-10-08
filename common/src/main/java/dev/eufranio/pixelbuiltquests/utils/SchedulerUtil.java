package dev.eufranio.pixelbuiltquests.utils;

import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SchedulerUtil {

    static ScheduledExecutorService asyncExecutor;
    static Executor syncExecutor;

    static void init() {
        asyncExecutor = Executors.newSingleThreadScheduledExecutor();
        syncExecutor = PixelBuiltQuests.server();
    }

    public static ScheduledExecutorService async() {
        if (asyncExecutor == null)
            init();
        return asyncExecutor;
    }

    public static Executor sync() {
        if (syncExecutor == null)
            init();
        return syncExecutor;
    }

}
