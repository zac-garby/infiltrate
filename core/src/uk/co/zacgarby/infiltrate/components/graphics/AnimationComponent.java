package uk.co.zacgarby.infiltrate.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class AnimationComponent implements Component {
    public static final ComponentMapper<AnimationComponent> mapper = ComponentMapper.getFor(AnimationComponent.class);

    public Animation animation = new Animation(0, 0);
    public int currentSlice = 0;
    public int delay = 1;
    public int framesLeft = delay;

    public AnimationComponent(int delay) {
        this.delay = delay;
    }

    public AnimationComponent set(Animation animation) {
        if (animation.startSlice != this.animation.startSlice || animation.endSlice != this.animation.endSlice) {
            this.animation = animation;
            this.currentSlice = animation.startSlice;
            this.framesLeft = 0;
        }

        return this;
    }

    public AnimationComponent set(int start, int end) {
        return set(new Animation(start, end));
    }

    public static class Animation {
        public int startSlice = 0, endSlice = 0;

        public Animation(int startSlice, int endSlice) {
            this.startSlice = startSlice;
            this.endSlice = endSlice;
        }
    }
}
