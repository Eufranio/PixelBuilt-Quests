package online.pixelbuilt.pbquests.utils;

import online.pixelbuilt.pbquests.persistence.QuestPersistenceService;
import org.spongepowered.api.Sponge;

/**
 * Created by NeumimTo on 5.11.2017.
 */
public class QuestService {

    private QuestPersistenceService persistenceService;


    public void initialize() {
        persistenceService = Sponge.getServiceManager().provide(QuestPersistenceService.class).get();
    }
}
