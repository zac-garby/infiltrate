package uk.co.zacgarby.infiltrate.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class TextureSliceComponent implements Component {
    public static final ComponentMapper<TextureSliceComponent> mapper = ComponentMapper.getFor(TextureSliceComponent.class);

    public TextureSliceComponent(int x, int y, int w, int h) {
        this.sliceX = x;
        this.sliceY = y;
        this.sliceWidth = w;
        this.sliceHeight = h;
    }

    public int sliceX, sliceY;
    public int sliceWidth, sliceHeight;
}
