package uk.co.zacgarby.infiltrate.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Rectangle;

public class InteractionComponent implements Component {
    public static final ComponentMapper<InteractionComponent> mapper = ComponentMapper.getFor(InteractionComponent.class);

    public float width, height;

    public InteractionComponent(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
