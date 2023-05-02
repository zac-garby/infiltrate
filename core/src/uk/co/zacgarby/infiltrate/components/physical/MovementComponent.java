package uk.co.zacgarby.infiltrate.components.physical;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements Component {
    public static final ComponentMapper<MovementComponent> mapper = ComponentMapper.getFor(MovementComponent.class);

    public Vector2 velocity = new Vector2();
}
