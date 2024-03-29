package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import jdk.javadoc.internal.doclets.formats.html.markup.Head;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementControlsComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementRecorderComponent;
import uk.co.zacgarby.infiltrate.components.physical.HeadingComponent;
import uk.co.zacgarby.infiltrate.components.physical.MovementComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;

public class MovementRecordingSystem extends IntervalIteratingSystem {
    private double timer = 0.0;

    public MovementRecordingSystem(float interval) {
        super(Family.all(
                MovementRecorderComponent.class,
                PositionComponent.class,
                MovementComponent.class,
                HeadingComponent.class
        ).get(), interval);
    }

    @Override
    protected void processEntity(Entity entity) {
        MovementRecorderComponent recorder = MovementRecorderComponent.mapper.get(entity);
        PositionComponent position = PositionComponent.mapper.get(entity);
        MovementComponent movement = MovementComponent.mapper.get(entity);
        HeadingComponent heading = HeadingComponent.mapper.get(entity);

        recorder.record(timer, position.position.cpy(), heading.heading.cpy(), movement.velocity.cpy());
    }

    @Override
    public void update(float deltaTime) {
        timer += deltaTime;

        super.update(deltaTime);
    }
}
