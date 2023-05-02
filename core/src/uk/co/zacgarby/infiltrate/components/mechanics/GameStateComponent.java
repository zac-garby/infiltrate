package uk.co.zacgarby.infiltrate.components.mechanics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class GameStateComponent implements Component {
    public static final ComponentMapper<GameStateComponent> mapper = ComponentMapper.getFor(GameStateComponent.class);

    public int currentTask = 0;

}
