package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import uk.co.zacgarby.infiltrate.components.Families;
import uk.co.zacgarby.infiltrate.components.MovementComponent;
import uk.co.zacgarby.infiltrate.components.PositionComponent;

public class PhysicsSystem extends IteratingSystem {
    public PhysicsSystem() {
        super(Families.physics);
        this.priority = 900;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementComponent movement = MovementComponent.mapper.get(entity);
        PositionComponent position = PositionComponent.mapper.get(entity);

        position.position.add(movement.velocity.cpy().scl(deltaTime));
    }
}
