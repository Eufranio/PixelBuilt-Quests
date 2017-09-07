package online.pixelbuilt.pbquests.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Frani on 06/09/2017.
 */
public class ChatUtils {

    public static Map<UUID, Long> timeleft = new HashMap<>();
    public static Map<UUID, ChatListener> users = new HashMap<>();

    public static void waitForResponse(Player p, String default_msg, ChatListener response) {
        p.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(default_msg));
        users.put(p.getUniqueId(), response);
        timeleft.put(p.getUniqueId(), System.currentTimeMillis());
    }

    @Listener
    public void onChat(MessageChannelEvent.Chat e, @Root Player p) {
        if(users.containsKey(p.getUniqueId())) {
            if(System.currentTimeMillis() - timeleft.get(p.getUniqueId()) > 10000) {
                users.remove(p.getUniqueId());
                timeleft.remove(p.getUniqueId());
                return;
            }
            ChatListener ch = users.get(p.getUniqueId());
            users.remove(p.getUniqueId());
            timeleft.remove(p.getUniqueId());
            ch.onChatComplete(p, e.getFormatter().getBody().toText().toPlain());
            e.setCancelled(true);
        }
    }

    public interface ChatListener {
        void onChatComplete(Player player, String message);
    }

}
