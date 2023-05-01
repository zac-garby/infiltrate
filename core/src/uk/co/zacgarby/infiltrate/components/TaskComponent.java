package uk.co.zacgarby.infiltrate.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public class TaskComponent implements Component, InteractionComponent.Interaction {
    public static final ComponentMapper<TaskComponent> mapper = ComponentMapper.getFor(TaskComponent.class);
    public final String description;

    public TaskComponent(String description) {
        this.description = description;
    }

    @Override
    public void doInteraction(Engine engine, Entity entity) {
        System.out.println("completed task: " + description);
        engine.removeEntity(entity);
    }
}
