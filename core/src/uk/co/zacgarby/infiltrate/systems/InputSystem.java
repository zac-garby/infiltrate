package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import uk.co.zacgarby.infiltrate.components.graphics.AnimationComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.InteractableComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.InteractionComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementControlsComponent;
import uk.co.zacgarby.infiltrate.components.physical.HeadingComponent;
import uk.co.zacgarby.infiltrate.components.physical.MovementComponent;

public class InputSystem extends IteratingSystem {
    public InputSystem() {
        super(Family.all(
                MovementControlsComponent.class,
                MovementComponent.class,
                AnimationComponent.class,
                HeadingComponent.class
        ).get());
        this.priority = 100;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementControlsComponent controls = MovementControlsComponent.mapper.get(entity);
        MovementComponent movement = MovementComponent.mapper.get(entity);
        AnimationComponent animation = AnimationComponent.mapper.get(entity);
        HeadingComponent heading = HeadingComponent.mapper.get(entity);

        Vector2 move = new Vector2();

        // calculate movement direction
        if (Gdx.input.isKeyPressed(controls.keyUp)) {
            move.y += 1f;
        }

        if (Gdx.input.isKeyPressed(controls.keyDown)) {
            move.y -= 1f;
        }

        if (Gdx.input.isKeyPressed(controls.keyLeft)) {
            move.x -= 1f;
        }

        if (Gdx.input.isKeyPressed(controls.keyRight)) {
            move.x += 1f;
        }

        move.nor().scl(controls.speed);

        // update torch direction
        if (move.len2() > 0.1) {
            heading.heading.interpolate(move.cpy(), 1.2f * deltaTime, Interpolation.circle).nor();
        }

        // set player velocity
        if (!Gdx.input.isKeyPressed(controls.noMove)) {
            movement.velocity = move;
        } else {
            movement.velocity.set(0.0f, 0.0f);
        }

        // do movement animations
        if (move.x < 0) {
            animation.set(controls.leftAnimation);
        } else if (move.x > 0) {
            animation.set(controls.rightAnimation);
        } else if (move.y < 0) {
            animation.set(controls.downAnimation);
        } else if (move.y > 0) {
            animation.set(controls.upAnimation);
        } else {
            animation.set(controls.stillAnimation);
        }

        // do interactions
        if (Gdx.input.isKeyJustPressed(controls.keyInteract)) {
            ImmutableArray<Entity> interactables = getEngine().getEntitiesFor(
                    Family.all(InteractableComponent.class).get()
            );

            if (interactables.size() > 0) {
                Entity interactable = interactables.first();
                doInteraction(interactable);
            }
        }
    }

    public void doInteraction(Entity entity) {
        for (Component c : entity.getComponents()) {
            if (c instanceof InteractionComponent.Interaction) {
                ((InteractionComponent.Interaction) c).doInteraction(getEngine(), entity);
            }
        }
    }
}
