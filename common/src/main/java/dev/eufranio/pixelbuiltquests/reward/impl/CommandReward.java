package dev.eufranio.pixelbuiltquests.reward.impl;

import com.google.common.collect.Lists;
import dev.eufranio.pixelbuiltquests.PixelBuiltQuests;
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
public class CommandReward implements BaseReward {

    @Setting
    public List<String> commands = Lists.newArrayList("give %player% minecraft:stone");

    @Override
    public void execute(UUID player, QuestReference reference) {
        List<String> cmds = commands.stream()
                .map(s -> s.replace("%player%", Util.player(player).getName().getString()))
                .toList();
        for (String command : cmds) {
            try {
                PixelBuiltQuests.server().getCommands()
                        .getDispatcher()
                        .execute(command, PixelBuiltQuests.server().createCommandSourceStack());
            } catch (Exception ex) {
                PixelBuiltQuests.instance().logger().error("Error dispatching command:");
                ex.printStackTrace();
            }
        }
    }

    @Override
    public RewardType getType() {
        return RewardTypes.COMMAND;
    }

}
