package uk.co.zacgarby.infiltrate.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;

public class TextureComponent implements Component {
    public static final ComponentMapper<TextureComponent> mapper = ComponentMapper.getFor(TextureComponent.class);

    public Texture texture;
    public float width, height;
    public float originX, originY;

    public TextureComponent(Texture texture, float width, float height) {
        this(texture, width, height, width / 2, height / 2);
    }

    public TextureComponent(Texture texture, float width, float height, float originX, float originY) {
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.originX = originX;
        this.originY = originY;
    }

    public TextureComponent(Texture texture, float width) {
        this(texture, width, width * (float) texture.getHeight() / (float) texture.getWidth());
    }

    public TextureComponent(Texture texture) {
        this(texture, texture.getWidth(), texture.getHeight());
    }
}
