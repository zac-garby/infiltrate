package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import uk.co.zacgarby.infiltrate.components.graphics.HiddenComponent;
import uk.co.zacgarby.infiltrate.components.graphics.TaskDescriptionComponent;
import uk.co.zacgarby.infiltrate.components.graphics.TextComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.GameStateComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.TaskComponent;

import java.util.Arrays;
import java.util.Comparator;

public class TaskSystem extends EntitySystem implements EntityListener {
    private ImmutableArray<Entity> tasks;
    private Entity currentTask;
    private Entity gameState;
    private final TaskCallback callback;

    public TaskSystem(TaskCallback callback) {
        this.callback = callback;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        engine.addEntityListener(
                Family.all(TaskComponent.class).get(),
                this
        );

        tasks = engine.getEntitiesFor(Family.all(TaskComponent.class).get());
        if (tasks.size() == 0) {
            engine.removeSystem(this);
            return;
        }

        currentTask = tasks.first();
        onNewTask();

        gameState = engine.getEntitiesFor(Family.all(GameStateComponent.class).get()).first();
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
    }

    private Entity getNextTask() {
        Object[] arr = tasks.toArray();

        Arrays.sort(arr, new Comparator<Object>() {
            @Override
            public int compare(Object e1, Object e2) {
                TaskComponent t1 = TaskComponent.mapper.get((Entity) e1);
                TaskComponent t2 = TaskComponent.mapper.get((Entity) e2);
                return t1.order - t2.order;
            }
        });

        return (Entity) arr[0];
    }

    public void onNewTask() {
        TaskComponent task = TaskComponent.mapper.get(currentTask);

        currentTask.remove(HiddenComponent.class);
        System.out.println("un-hidden task");

        Entity taskTextEntity = getEngine().getEntitiesFor(
                Family.all(TaskDescriptionComponent.class, TextComponent.class).get()).first();
        TextComponent taskText = TextComponent.mapper.get(taskTextEntity);

        taskText.text = "* " + task.description;
    }

    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {
        // when a task component is removed
        // (i.e. completed)

        TaskComponent task = TaskComponent.mapper.get(entity);
        callback.onTaskComplete(task);

        GameStateComponent state = GameStateComponent.mapper.get(gameState);
        state.currentTask++;

        if (tasks.size() <= 0) {
            // all tasks finished
            System.out.println("all tasks finished");
            callback.onAllTasksComplete();

            Entity taskTextEntity = getEngine().getEntitiesFor(
                    Family.all(TaskDescriptionComponent.class, TextComponent.class).get()
            ).first();

            TextComponent taskText = TextComponent.mapper.get(taskTextEntity);
            taskText.text = "(no tasks remaining)";

            return;
        }

        currentTask = getNextTask();
        onNewTask();
    }

    public interface TaskCallback {
        void onAllTasksComplete();
        void onTaskComplete(TaskComponent task);
    }
}
