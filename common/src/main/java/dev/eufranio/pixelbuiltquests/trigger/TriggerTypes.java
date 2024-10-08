package dev.eufranio.pixelbuiltquests.trigger;

import dev.eufranio.pixelbuiltquests.trigger.creator.LocationTriggerCreator;
import dev.eufranio.pixelbuiltquests.trigger.creator.SelectEntityTriggerCreator;
import dev.eufranio.pixelbuiltquests.trigger.listener.*;

import java.util.List;

public class TriggerTypes {

    public static final TriggerType WALK = new TriggerType(
            "pbq:walk",
            "Walk",
            new WalkTriggerListenerProvider(),
            new LocationTriggerCreator());

    public static final TriggerType CLICK = new TriggerType(
            "pbq:click",
            "Click",
            new ClickTriggerListenerProvider(),
            new LocationTriggerCreator());

    public static final TriggerType RIGHT_CLICK = new TriggerType(
            "pbq:right_click",
            "Right Click",
            new RightClickTriggerListenerProvider(),
            new LocationTriggerCreator());

    public static final TriggerType LEFT_CLICK = new TriggerType(
            "pbq:left_click",
            "Left Click",
            new LeftClickTriggerListenerProvider(),
            new LocationTriggerCreator());

    public static final TriggerType CLICK_ENTITY = new TriggerType(
            "pbq:click_entity",
            "Right or left click an entity",
            new ClickEntityTriggerListenerProvider(),
            new SelectEntityTriggerCreator());

    public static final TriggerType MAINHAND_CLICK_ENTITY = new TriggerType(
            "pbq:mainhand_click_entity",
            "Click an entity with the main hand",
            new MainClickEntityTriggerListenerProvider(),
            new SelectEntityTriggerCreator());

    public static final TriggerType OFFHAND_CLICK_ENTITY = new TriggerType(
            "pbq:offhand_click_entity",
            "Click an entity with the offhand",
            new OffClickEntityTriggerListenerProvider(),
            new SelectEntityTriggerCreator());

    public static List<TriggerType> defaults() {
        return List.of(WALK, CLICK, RIGHT_CLICK, LEFT_CLICK, CLICK_ENTITY, MAINHAND_CLICK_ENTITY, OFFHAND_CLICK_ENTITY);
    }

}
