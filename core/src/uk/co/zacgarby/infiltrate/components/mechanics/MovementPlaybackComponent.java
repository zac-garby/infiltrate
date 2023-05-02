package uk.co.zacgarby.infiltrate.components.mechanics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import java.util.Queue;

public class MovementPlaybackComponent implements Component {
    public static final ComponentMapper<MovementPlaybackComponent> mapper = ComponentMapper.getFor(MovementPlaybackComponent.class);

    public final Queue<MovementRecorderComponent.Record> records;
    public MovementRecorderComponent.Record currentRecord, nextRecord;

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
