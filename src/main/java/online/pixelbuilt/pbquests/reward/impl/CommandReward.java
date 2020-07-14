package online.pixelbuilt.pbquests.reward.impl;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.storage.sql.PlayerData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class CommandReward implements BaseReward<CommandReward> {

    @Setting
    public List<String> commands = Lists.newArrayList("give %player% minecraft:stone");

    @Override
    public void execute(PlayerData data, QuestLine line, Quest quest) {
        List<String> cmds = commands.stream()
                .map(s -> s.replace("%player%", data.getUser().getName()))
                .collect(Collectors.toList());
        for (String command : cmds) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
        }
    }
}
