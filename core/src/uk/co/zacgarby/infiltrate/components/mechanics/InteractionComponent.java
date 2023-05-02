package uk.co.zacgarby.infiltrate.components.mechanics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public class InteractionComponent implements Component {
    public static final ComponentMapper<InteractionComponent> mapper = ComponentMapper.getFor(InteractionComponent.class);

    public float width, height;

    public InteractionComponent(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public interface Interaction {
        void doInteraction(Engine engine, Entity entity);
    }
}
