package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import uk.co.zacgarby.infiltrate.components.AnimationComponent;
import uk.co.zacgarby.infiltrate.components.TextureSliceComponent;

public class AnimationSystem extends IntervalIteratingSystem {
    public AnimationSystem(float baseInterval) {
        super(Family.all(AnimationComponent.class, TextureSliceComponent.class).get(), baseInterval);
    }

    @Override
    protected void processEntity(Entity entity) {
        AnimationComponent animation = AnimationComponent.mapper.get(entity);
        TextureSliceComponent slice = TextureSliceComponent.mapper.get(entity);

        animation.framesLeft--;

        if (animation.framesLeft <= 0) {
            animation.framesLeft = animation.delay;
            animation.currentSlice++;

            if (animation.currentSlice > animation.animation.endSlice) {
                animation.currentSlice = animation.animation.startSlice;
            }

            slice.sliceX = animation.currentSlice;
        }
    }
}
