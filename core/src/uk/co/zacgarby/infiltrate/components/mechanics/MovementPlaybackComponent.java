package uk.co.zacgarby.infiltrate.components.mechanics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import uk.co.zacgarby.infiltrate.components.graphics.AnimationComponent;

import java.util.Queue;

public class MovementPlaybackComponent implements Component {
    public static final ComponentMapper<MovementPlaybackComponent> mapper = ComponentMapper.getFor(MovementPlaybackComponent.class);

    public final Queue<MovementRecorderComponent.Record> records;
    public MovementRecorderComponent.Record currentRecord, nextRecord;

    public AnimationComponent.Animation leftAnimation = new AnimationComponent.Animation(3, 4);
    public AnimationComponent.Animation rightAnimation = new AnimationComponent.Animation(1, 2);
    public AnimationComponent.Animation upAnimation = new AnimationComponent.Animation(7, 8);
    public AnimationComponent.Animation downAnimation = new AnimationComponent.Animation(5, 6);
    public AnimationComponent.Animation stillAnimation = new AnimationComponent.Animation(0, 0);

    public MovementPlaybackComponent(Queue<MovementRecorderComponent.Record> records) {
        this.records = records;
        step();
        step();
    }

    public void step() {
        currentRecord = nextRecord;
        nextRecord = this.records.poll();
    }
}
