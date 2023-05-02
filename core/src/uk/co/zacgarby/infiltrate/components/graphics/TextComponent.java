package uk.co.zacgarby.infiltrate.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class TextComponent implements Component {
    public static final ComponentMapper<TextComponent> mapper = ComponentMapper.getFor(TextComponent.class);

    public String text;
    public int x, y;
    public boolean alignLeft = true;

    public TextComponent(String text, int x, int y, boolean alignLeft) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.alignLeft = alignLeft;
    }

    public TextComponent(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }
}
