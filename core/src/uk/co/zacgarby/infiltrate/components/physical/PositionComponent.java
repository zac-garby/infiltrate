package uk.co.zacgarby.infiltrate.components.physical;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {
    public static final ComponentMapper<PositionComponent> mapper = ComponentMapper.getFor(PositionComponent.class);
    public Vector2 position;

    public PositionComponent(float x, float y) {
        this.position = new Vector2(x, y);
    }
}
