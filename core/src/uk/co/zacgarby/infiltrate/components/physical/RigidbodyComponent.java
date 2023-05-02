package uk.co.zacgarby.infiltrate.components.physical;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.physics.box2d.*;

public class RigidbodyComponent implements Component {
    public static final ComponentMapper<RigidbodyComponent> mapper = ComponentMapper.getFor(RigidbodyComponent.class);

    public float xOffset, yOffset;
    public BodyDef bodyDef;
    public FixtureDef fixtureDef;
    public Body body;
    public Fixture fixture;

    public RigidbodyComponent(FixtureDef fixtureDef, float xOffset, float yOffset) {
        this.fixtureDef = fixtureDef;
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        bodyDef = new BodyDef();
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
    }

    public RigidbodyComponent(float radius, float xOffset, float yOffset) {
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f;

        this.fixtureDef = fixtureDef;
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        bodyDef = new BodyDef();
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
    }

    public RigidbodyComponent(float radius) {
        this(radius, 0f, 0f);
    }
}
