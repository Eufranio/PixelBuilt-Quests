package online.pixelbuilt.pbquests.reward.impl;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class TeleportReward implements BaseReward<TeleportReward> {

    @Setting
    public String location = "0,0,0,world";

    @Override
    public void execute(PlayerData data, QuestLine line, Quest quest) {
        data.getUser().getPlayer().ifPresent(player -> {
            String[] string = location.split(",");
            World world = Sponge.getServer().getWorld(string[3]).orElse(null);
            if (world == null) return;
            Location<World> loc = new Location<>(world, Integer.parseInt(string[0]), Integer.parseInt(string[1]), Integer.parseInt(string[2]));
            player.setLocation(loc);
        });
    }
}
