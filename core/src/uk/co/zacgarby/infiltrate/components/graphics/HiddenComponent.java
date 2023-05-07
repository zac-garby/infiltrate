package uk.co.zacgarby.infiltrate.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class HiddenComponent implements Component {
    public static ComponentMapper<HiddenComponent> mapper = ComponentMapper.getFor(HiddenComponent.class);
}
