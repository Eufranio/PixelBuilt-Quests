package dev.eufranio.pixelbuiltquests.reward.impl;

import com.google.common.collect.Lists;
import dev.eufranio.pixelbuiltquests.quest.QuestReference;
import dev.eufranio.pixelbuiltquests.reward.BaseReward;
import dev.eufranio.pixelbuiltquests.reward.RewardType;
import dev.eufranio.pixelbuiltquests.reward.RewardTypes;
import dev.eufranio.pixelbuiltquests.utils.Util;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;
import java.util.UUID;

@ConfigSerializable
public class MessageReward implements BaseReward {

    @Setting
    public List<String> messages = Lists.newArrayList("&aSuccessfully completed quest!");

    @Override
    public RewardType getType() {
        return RewardTypes.MESSAGE;
    }

    @Override
    public void execute(UUID player, QuestReference reference) {
        messages.stream()
                .map(s -> s.replace("%player%", Util.player(player).getName().getString())
                           .replace("%quest%", reference.getQuest().getName()))
                .map(Util::text)
                .forEach(msg -> Util.player(player).sendSystemMessage(msg));
    }
}