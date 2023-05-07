package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import uk.co.zacgarby.infiltrate.components.ui.ClockComponent;
import uk.co.zacgarby.infiltrate.components.ui.UITextComponent;

public class ClockSystem extends IteratingSystem {
    public float time = 0.0f;

    public ClockSystem(float time) {
        super(Family.all(
                ClockComponent.class,
                UITextComponent.class
        ).get());

        this.time = time;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        UITextComponent textComponent = UITextComponent.mapper.get(entity);
        textComponent.text = String.format("%02d:%02d", getMinutes(), getSeconds());
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;

        super.update(deltaTime);
    }

    public int getMinutes() {
        return MathUtils.floor(time / 60.0f);
    }

    public int getSeconds() {
        return (int) (time - 60.0f * getMinutes());
    }
}
