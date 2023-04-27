package uk.co.zacgarby.infiltrate.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;

public class TextureComponent implements Component {
    public static final ComponentMapper<TextureComponent> mapper = ComponentMapper.getFor(TextureComponent.class);

    public Texture texture;
    public float width, height;

    public TextureComponent(Texture texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public TextureComponent(Texture texture, float width) {
        this(texture, width, width * (float) texture.getHeight() / (float) texture.getWidth());
    }
}
