package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import uk.co.zacgarby.infiltrate.components.graphics.AnimationComponent;
import uk.co.zacgarby.infiltrate.components.graphics.HiddenComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.InteractableComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.InteractionComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.PlayerComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;

public class InteractionSystem extends IteratingSystem {
    public static final float INTERACTION_DIST = 9.0f;
    private Entity player;

    public InteractionSystem() {
        super(Family
                .all(InteractionComponent.class)
                .exclude(HiddenComponent.class).get(), 50);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        InteractionComponent interaction = InteractionComponent.mapper.get(entity);
        AnimationComponent anim = AnimationComponent.mapper.get(entity);
        boolean alreadyInteractable = InteractableComponent.mapper.has(entity);

        Vector2 pos = PositionComponent.mapper.get(entity).position;
        Vector2 playerPos = PositionComponent.mapper.get(player).position;

        float hw = interaction.width / 2;
        float hh = interaction.height / 2;
        float dx = Math.max(pos.x - hw - playerPos.x, Math.max(playerPos.x - pos.x - hw, 0));
        float dy = Math.max(pos.y - hh - playerPos.y, Math.max(playerPos.y - pos.y - hh, 0));
        float distSqr = dx*dx + dy*dy;

        if (distSqr <= INTERACTION_DIST * INTERACTION_DIST) {
            if (!alreadyInteractable) {
                anim.set(0, 1);
                entity.add(new InteractableComponent());
            }
        } else {
            anim.set(0, 0);
            entity.remove(InteractableComponent.class);
        }
    }

    @Override
    public void update(float deltaTime) {
        player = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        super.update(deltaTime);
    }
}
