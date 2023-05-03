package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementControlsComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementPlaybackComponent;
import uk.co.zacgarby.infiltrate.components.physical.HeadingComponent;
import uk.co.zacgarby.infiltrate.components.physical.MovementComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;

public class MovementPlaybackSystem extends IteratingSystem {
    private double time = 0.0;

    public MovementPlaybackSystem() {
        super(Family.all(
                MovementPlaybackComponent.class,
                PositionComponent.class,
                HeadingComponent.class
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
            }
        } else {
            getEngine().removeEntity(entity);
        }
    }
}
