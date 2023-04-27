package uk.co.zacgarby.infiltrate.components;

import com.badlogic.ashley.core.Family;

public abstract class Families {
    public static final Family renderable = Family.all(TextureComponent.class, PositionComponent.class).get();
    public static final Family physics = Family.all(MovementComponent.class, PositionComponent.class).get();
    public static final Family control = Family.all(MovementControlsComponent.class, MovementComponent.class, AnimationComponent.class).get();
    public static final Family animated = Family.all(AnimationComponent.class, TextureSliceComponent.class).get();
    public static final Family cameraFollow = Family.all(CameraFollowComponent.class, PositionComponent.class).get();
}
