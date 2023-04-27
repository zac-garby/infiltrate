package uk.co.zacgarby.infiltrate.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Input;

public class MovementControlsComponent implements Component {
    public static final ComponentMapper<MovementControlsComponent> mapper = ComponentMapper.getFor(MovementControlsComponent.class);

    public int keyLeft = Input.Keys.LEFT;
    public int keyRight = Input.Keys.RIGHT;
    public int keyUp = Input.Keys.UP;
    public int keyDown = Input.Keys.DOWN;
    public float speed;
    public AnimationComponent.Animation leftAnimation = new AnimationComponent.Animation(3, 4);
    public AnimationComponent.Animation rightAnimation = new AnimationComponent.Animation(1, 2);
    public AnimationComponent.Animation upAnimation = new AnimationComponent.Animation(7, 8);
    public AnimationComponent.Animation downAnimation = new AnimationComponent.Animation(5, 6);
    public AnimationComponent.Animation stillAnimation = new AnimationComponent.Animation(0, 0);

    public MovementControlsComponent(float speed) {
        this.speed = speed;
    }
}
