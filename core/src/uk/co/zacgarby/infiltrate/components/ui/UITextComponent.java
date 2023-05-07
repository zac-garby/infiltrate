package uk.co.zacgarby.infiltrate.components.ui;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;

public class UITextComponent implements Component {
    public static final ComponentMapper<UITextComponent> mapper = ComponentMapper.getFor(UITextComponent.class);

    public String text;
    public int x, y;
    public Align align = Align.Left;
    public Effect effect = null;
    public Color colour;

    public UITextComponent(String text, int x, int y, Align align, Effect effect, Color colour) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.align = align;
        this.effect = effect;
        this.colour = colour;
    }

    public UITextComponent(String text, int x, int y, Align align) {
        this(text, x, y, align, null, Color.WHITE);
    }

    public UITextComponent(String text, int x, int y) {
        this(text, x, y, Align.Left);
    }

    public UITextComponent(String text, int x, int y, Align align, Effect effect) {
        this(text, x, y, align, effect, Color.WHITE);
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
