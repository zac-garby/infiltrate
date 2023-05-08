package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import uk.co.zacgarby.infiltrate.components.ui.ClockComponent;
import uk.co.zacgarby.infiltrate.components.ui.UITextComponent;

public class ClockSystem extends IteratingSystem {
    public float totalTime;
    public float levelTime;

    public ClockSystem(float totalTime) {
        super(Family.all(
                ClockComponent.class,
                UITextComponent.class
        ).get());

        this.totalTime = totalTime;
        this.levelTime = 0.0f;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        UITextComponent textComponent = UITextComponent.mapper.get(entity);
        ClockComponent clockComponent = ClockComponent.mapper.get(entity);

        textComponent.text = getTimeString(clockComponent.isTotalTime ? totalTime : levelTime);
    }

    @Override
    public void update(float deltaTime) {
        totalTime += deltaTime;
        levelTime += deltaTime;

        super.update(deltaTime);
    }

    public static String getTimeString(float t) {
        return String.format("%02d:%02d", getMinutes(t), getSeconds(t));
    }

    public static int getMinutes(float t) {
        return MathUtils.floor(t / 60.0f);
    }

    public static int getSeconds(float t) {
        return (int) (t - 60.0f * getMinutes(t));
    }
}
