package uk.co.zacgarby.infiltrate.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class UITextComponent implements Component {
    public static final ComponentMapper<UITextComponent> mapper = ComponentMapper.getFor(UITextComponent.class);

    public String text;
    public int x, y;
    public Align align = Align.Left;
    public Effect effect = null;

    public UITextComponent(String text, int x, int y, Align align, Effect effect) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.align = align;
        this.effect = effect;
    }

    public UITextComponent(String text, int x, int y, Align align) {
        this(text, x, y, align, null);
    }

    public UITextComponent(String text, int x, int y) {
        this(text, x, y, Align.Left);
    }

    public enum Align {
        Left,
        Right,
        Center
    }

    public enum Effect {
        Flashing,
        TypeOut
    }
}
