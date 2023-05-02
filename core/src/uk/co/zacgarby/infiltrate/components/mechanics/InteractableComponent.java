package uk.co.zacgarby.infiltrate.components.mechanics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class InteractableComponent implements Component {
    public static final ComponentMapper<InteractableComponent> mapper = ComponentMapper.getFor(InteractableComponent.class);
}
