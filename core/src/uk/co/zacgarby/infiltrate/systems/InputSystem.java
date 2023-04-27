package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import uk.co.zacgarby.infiltrate.components.AnimationComponent;
import uk.co.zacgarby.infiltrate.components.Families;
import uk.co.zacgarby.infiltrate.components.MovementComponent;
import uk.co.zacgarby.infiltrate.components.MovementControlsComponent;

public class InputSystem extends IteratingSystem {
    public InputSystem() {
        super(Families.control);
        this.priority = 100;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementControlsComponent controls = MovementControlsComponent.mapper.get(entity);
        MovementComponent movement = MovementComponent.mapper.get(entity);
        AnimationComponent animation = AnimationComponent.mapper.get(entity);

        Vector2 move = new Vector2();

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

        movement.velocity = move.nor().scl(controls.speed);

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
    }
}
