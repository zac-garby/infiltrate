package uk.co.zacgarby.infiltrate.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class PlayerComponent implements Component {
    public static final ComponentMapper<PlayerComponent> mapper = ComponentMapper.getFor(PlayerComponent.class);
}
