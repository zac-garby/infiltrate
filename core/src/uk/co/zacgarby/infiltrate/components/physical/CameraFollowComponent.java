package uk.co.zacgarby.infiltrate.components.physical;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Camera;

public class CameraFollowComponent implements Component {
    public static final ComponentMapper<CameraFollowComponent> mapper = ComponentMapper.getFor(CameraFollowComponent.class);

    public Camera camera;

    public CameraFollowComponent(Camera camera) {
        this.camera = camera;
    }
}
