package uk.co.zacgarby.infiltrate.components.ui;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class ClockComponent implements Component {
    public static final ComponentMapper<ClockComponent> mapper = ComponentMapper.getFor(ClockComponent.class);
}
