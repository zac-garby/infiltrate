package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import uk.co.zacgarby.infiltrate.components.graphics.AnimationComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementControlsComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementPlaybackComponent;
import uk.co.zacgarby.infiltrate.components.physical.HeadingComponent;
import uk.co.zacgarby.infiltrate.components.physical.MovementComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;
import uk.co.zacgarby.infiltrate.components.ui.TracerComponent;

public class MovementPlaybackSystem extends IteratingSystem {
    private double time = 0.0;

    public MovementPlaybackSystem() {
        super(Family.all(
                MovementPlaybackComponent.class,
                PositionComponent.class,
                HeadingComponent.class,
                AnimationComponent.class
        ).get());
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementPlaybackComponent playback = MovementPlaybackComponent.mapper.get(entity);
        PositionComponent position = PositionComponent.mapper.get(entity);
        HeadingComponent heading = HeadingComponent.mapper.get(entity);
        AnimationComponent animation = AnimationComponent.mapper.get(entity);

        if (playback.nextRecord != null) {
            if (time >= playback.nextRecord.time) {
                playback.step();
            } else {
                double t = (time - playback.currentRecord.time) /
                        (playback.nextRecord.time - playback.currentRecord.time);

                position.position.set(playback
                        .currentRecord.position.cpy()
                        .interpolate(playback.nextRecord.position, (float) t, Interpolation.linear));

                heading.heading.set(playback
                        .currentRecord.heading.cpy()
                        .interpolate(playback.nextRecord.heading, (float) t, Interpolation.circle));

                Vector2 move = playback.currentRecord.velocity;
                if (move.x < 0) {
                    animation.set(playback.leftAnimation);
                } else if (move.x > 0) {
                    animation.set(playback.rightAnimation);
                } else if (move.y < 0) {
                    animation.set(playback.downAnimation);
                } else if (move.y > 0) {
                    animation.set(playback.upAnimation);
                } else {
                    animation.set(playback.stillAnimation);
                }
            }
        } else {
            getEngine().removeEntity(entity);

            ImmutableArray<Entity> tracers = getEngine().getEntitiesFor(Family.all(TracerComponent.class).get());
            for (Entity e : tracers) {
                TracerComponent tracer = TracerComponent.mapper.get(e);
                if (tracer.agent == entity) {
                    getEngine().removeEntity(e);
                }
            }
        }
    }
}
