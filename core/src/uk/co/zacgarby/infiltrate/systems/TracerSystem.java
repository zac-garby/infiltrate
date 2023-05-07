package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import uk.co.zacgarby.infiltrate.components.graphics.HiddenComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;
import uk.co.zacgarby.infiltrate.components.ui.TracerComponent;
import uk.co.zacgarby.infiltrate.components.ui.UITextComponent;

public class TracerSystem extends IteratingSystem {
    public static float TRACER_DIST = 12 * 12f;
    private final Entity player;

    public TracerSystem(Entity player) {
        super(Family.all(
                TracerComponent.class,
                UITextComponent.class
        ).get());

        this.player = player;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Vector2 playerPos = PositionComponent.mapper.get(player).position;
        TracerComponent tracer = TracerComponent.mapper.get(entity);
        Vector2 agentPos = PositionComponent.mapper.get(tracer.agent).position;

        Vector2 diff = agentPos.cpy().sub(playerPos);
        if (diff.len2() <= TRACER_DIST * TRACER_DIST) {
            UITextComponent text = UITextComponent.mapper.get(entity);
//            diff.nor().scl(TorchDetectionSystem.RANGE);
            diff.x = Math.max(-70, Math.min(70, diff.x));
            diff.y = Math.max(-70, Math.min(70, diff.y));
            text.x = (int) (100 + diff.x);
            text.y = (int) (100 + diff.y);

            entity.remove(HiddenComponent.class);
        } else {
            if (!HiddenComponent.mapper.has(entity)) {
                entity.add(new HiddenComponent());
            }
        }
    }
}
