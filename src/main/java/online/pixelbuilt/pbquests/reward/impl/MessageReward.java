package online.pixelbuilt.pbquests.reward.impl;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import online.pixelbuilt.pbquests.quest.Quest;
import online.pixelbuilt.pbquests.quest.QuestLine;
import online.pixelbuilt.pbquests.reward.BaseReward;
import online.pixelbuilt.pbquests.utils.Util;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Frani on 20/01/2019.
 */
@ConfigSerializable
public class MessageReward implements BaseReward<MessageReward> {

    @Setting
    public List<String> messages = Lists.newArrayList("&aSuccessfully completed quest!");

    @Override
    public void execute(Player player, Quest quest, QuestLine line, int questId) {
        messages.stream()
                .map(s -> s.replace("%player%", player.getName())
                           .replace("%quest%", quest.displayName))
                .map(Util::toText)
                .forEach(player::sendMessage);
    }
}