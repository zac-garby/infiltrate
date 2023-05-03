package uk.co.zacgarby.infiltrate.components.physical;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public class HeadingComponent implements Component {
    public static final ComponentMapper<HeadingComponent> mapper = ComponentMapper.getFor(HeadingComponent.class);

    public Vector2 heading;

    public HeadingComponent(Vector2 heading) {
        this.heading = heading;
    }
}
