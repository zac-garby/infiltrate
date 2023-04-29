package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import uk.co.zacgarby.infiltrate.components.*;

public class PhysicsSystem extends EntitySystem implements EntityListener {
    public PhysicsWorldComponent world;

    private ImmutableArray<Entity> entities;

    public PhysicsSystem(Entity world) {
        this.priority = 1100;
        this.world = PhysicsWorldComponent.mapper.get(world);
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Families.physics);
        engine.addEntityListener(Families.physics, this);
    }

    @Override
    public void update(float dt) {
        for (Entity e : entities) {
            if (MovementComponent.mapper.has(e)) {
                MovementComponent movement = MovementComponent.mapper.get(e);
                RigidbodyComponent rb = RigidbodyComponent.mapper.get(e);

                rb.body.setLinearVelocity(movement.velocity);
            }
        }

        world.world.step(dt, 6, 2);

        for (Entity e : entities) {
            RigidbodyComponent rb = RigidbodyComponent.mapper.get(e);
            PositionComponent position = PositionComponent.mapper.get(e);

            position.position.set(rb.body.getPosition().sub(rb.xOffset, rb.yOffset));
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        RigidbodyComponent rb = RigidbodyComponent.mapper.get(entity);
        PositionComponent position = PositionComponent.mapper.get(entity);

        rb.bodyDef.position.set(position.position).add(rb.xOffset, rb.yOffset);

        Body body = world.world.createBody(rb.bodyDef);
        Fixture fixture = body.createFixture(rb.fixtureDef);

        rb.body = body;
        rb.fixture = fixture;

        System.out.println("added entity to the physics system");
    }

    @Override
    public void entityRemoved(Entity entity) {
        RigidbodyComponent rb = RigidbodyComponent.mapper.get(entity);

        world.world.destroyBody(rb.body);
    }
}
