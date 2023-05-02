package uk.co.zacgarby.infiltrate.components.mechanics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import uk.co.zacgarby.infiltrate.components.graphics.AnimationComponent;

public class MovementControlsComponent implements Component {
    public static final ComponentMapper<MovementControlsComponent> mapper = ComponentMapper.getFor(MovementControlsComponent.class);

    public int keyLeft = Input.Keys.A;
    public int keyRight = Input.Keys.D;
    public int keyUp = Input.Keys.W;
    public int keyDown = Input.Keys.S;
    public int keyInteract = Input.Keys.SPACE;

    public float speed;

    public Vector2 heading = new Vector2(1.0f, 0.0f).nor();
    public AnimationComponent.Animation leftAnimation = new AnimationComponent.Animation(3, 4);
    public AnimationComponent.Animation rightAnimation = new AnimationComponent.Animation(1, 2);
    public AnimationComponent.Animation upAnimation = new AnimationComponent.Animation(7, 8);
    public AnimationComponent.Animation downAnimation = new AnimationComponent.Animation(5, 6);
    public AnimationComponent.Animation stillAnimation = new AnimationComponent.Animation(0, 0);

    public MovementControlsComponent(float speed) {
        this.speed = speed;
    }
}
