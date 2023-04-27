package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import uk.co.zacgarby.infiltrate.components.CameraFollowComponent;
import uk.co.zacgarby.infiltrate.components.Families;
import uk.co.zacgarby.infiltrate.components.PositionComponent;

public class CameraFollowSystem extends IteratingSystem {
    public CameraFollowSystem() {
        super(Families.cameraFollow);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CameraFollowComponent follow = CameraFollowComponent.mapper.get(entity);
        PositionComponent position = PositionComponent.mapper.get(entity);

        follow.camera.position.set(position.position.x, position.position.y, 0);
    }
}
