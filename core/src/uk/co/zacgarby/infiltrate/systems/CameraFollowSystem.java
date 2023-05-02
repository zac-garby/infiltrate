package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import uk.co.zacgarby.infiltrate.components.CameraFollowComponent;
import uk.co.zacgarby.infiltrate.components.PositionComponent;

public class CameraFollowSystem extends IteratingSystem {
    public CameraFollowSystem() {
        super(Family.all(CameraFollowComponent.class, PositionComponent.class).get(), 1300);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CameraFollowComponent follow = CameraFollowComponent.mapper.get(entity);
        PositionComponent position = PositionComponent.mapper.get(entity);

        follow.camera.position.set(Math.round(position.position.x), Math.round(position.position.y), 0);
    }
}
