package online.pixelbuilt.pbquests.persistence;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.SpongeExecutorService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 4.11.2017.
 */
public class QuestPersistenceService {

    private IQuestDAO questDAO;

    private SpongeExecutorService executor;

    public QuestPersistenceService(IQuestDAO dao) {
        this.questDAO = dao;
        executor = Sponge.getServiceManager().provide(SpongeExecutorService.class).get();
    }

    public void getProgress(Player player, String questLine, Consumer<Integer> cb) {
        getProgress(player.getUniqueId(), questLine, cb);
    }

    public void getProgress(UUID uuid, String questLine, Consumer<Integer> cb) {
        CompletableFuture
                .supplyAsync(() -> questDAO.getProgress(uuid, questLine))
                .thenAcceptAsync(cb, executor);
    }

    public void setProgressLevel(Player p, String questLine, int progress, Runnable cb) {
        CompletableFuture
                .runAsync(() -> questDAO.setProgressLevel(p, questLine, progress))
                .thenRunAsync(cb, executor);
    }

    public void getQuestLineFromNPC(Entity npc, Consumer<String> cb) {
        CompletableFuture
                .supplyAsync(() -> questDAO.getQuestLineFromNPC(npc))
                .thenAcceptAsync(cb, executor);
    }

    public void getQuestIdFromNPC(Entity npc, Consumer<Integer> cb) {
        CompletableFuture
                .supplyAsync(() -> questDAO.getQuestIdFromNPC(npc))
                .thenAcceptAsync(cb, executor);
    }

    public void addNpc(String questLine, int questId, Entity npc) {
        CompletableFuture
                .runAsync(() -> questDAO.addNpc(questLine, questId, npc));
    }

    public void removeNpc(Entity npc) {
        CompletableFuture
                .runAsync(() -> questDAO.removeNpc(npc));
    }

}
