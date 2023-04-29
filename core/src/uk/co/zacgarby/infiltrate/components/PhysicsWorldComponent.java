package uk.co.zacgarby.infiltrate.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsWorldComponent implements Component {
    public static final ComponentMapper<PhysicsWorldComponent> mapper = ComponentMapper.getFor(PhysicsWorldComponent.class);

    public World world;

    public PhysicsWorldComponent() {
        world = new World(new Vector2(0, 0), true);
    }
}
