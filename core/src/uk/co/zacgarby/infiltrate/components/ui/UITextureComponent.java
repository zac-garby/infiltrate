package uk.co.zacgarby.infiltrate.components.ui;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;

public class UITextureComponent implements Component {
    public static final ComponentMapper<UITextureComponent> mapper = ComponentMapper.getFor(UITextureComponent.class);

    public final Texture texture;
    public final int x, y;

    public UITextureComponent(Texture texture, int x, int y) {
        this.texture = texture;
        this.x = x;
        this.y = y;
    }
}
