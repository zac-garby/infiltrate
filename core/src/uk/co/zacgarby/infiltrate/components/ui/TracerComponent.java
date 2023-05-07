package uk.co.zacgarby.infiltrate.components.ui;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class TracerComponent implements Component {
    public static final ComponentMapper<TracerComponent> mapper = ComponentMapper.getFor(TracerComponent.class);
    
    public final Entity agent;
    
    public TracerComponent(Entity agent) {
        this.agent = agent;
    }
}
