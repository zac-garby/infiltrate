package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementControlsComponent;
import uk.co.zacgarby.infiltrate.components.physical.MovementComponent;
import uk.co.zacgarby.infiltrate.components.physical.PhysicsWorldComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;
import uk.co.zacgarby.infiltrate.components.physical.RigidbodyComponent;

public class PhysicsSystem extends EntitySystem implements EntityListener {
    public static final float AIR_RESISTANCE = 3f;
    public PhysicsWorldComponent world;

    private ImmutableArray<Entity> entities;

    public PhysicsSystem(Entity world) {
        this.priority = 1100;
        this.world = PhysicsWorldComponent.mapper.get(world);
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(RigidbodyComponent.class, PositionComponent.class).get());
        engine.addEntityListener(Family.all(RigidbodyComponent.class, PositionComponent.class).get(), this);
    }

    @Override
    public void update(float dt) {
        for (Entity e : entities) {
            if (MovementComponent.mapper.has(e) && MovementControlsComponent.mapper.has(e)) {
                MovementComponent movement = MovementComponent.mapper.get(e);
                RigidbodyComponent rb = RigidbodyComponent.mapper.get(e);
                PositionComponent position = PositionComponent.mapper.get(e);

                if (movement.velocity.x > 0 && rb.body.getLinearVelocity().x > -Math.abs(movement.velocity.x)) {
                    rb.body.applyLinearImpulse(new Vector2(movement.velocity.x, 0f), rb.body.getPosition(), true);
                } else if (movement.velocity.x < 0 && rb.body.getLinearVelocity().x < Math.abs(movement.velocity.x)) {
                    rb.body.applyLinearImpulse(new Vector2(movement.velocity.x, 0f), rb.body.getPosition(), true);
                }

                if (movement.velocity.y > 0 && rb.body.getLinearVelocity().y > -Math.abs(movement.velocity.y)) {
                    rb.body.applyLinearImpulse(new Vector2(0f, movement.velocity.y), rb.body.getPosition(), true);
                } else if (movement.velocity.y < 0 && rb.body.getLinearVelocity().y < Math.abs(movement.velocity.y)) {
                    rb.body.applyLinearImpulse(new Vector2(0f, movement.velocity.y), rb.body.getPosition(), true);
                }

                rb.body.applyLinearImpulse(rb.body.getLinearVelocity().cpy().scl(-AIR_RESISTANCE), rb.body.getPosition(), true);
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
