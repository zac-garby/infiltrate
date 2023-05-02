package uk.co.zacgarby.infiltrate.components.mechanics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayDeque;
import java.util.Queue;

public class MovementRecorder implements Component {
    public static final ComponentMapper<MovementRecorder> mapper = ComponentMapper.getFor(MovementRecorder.class);
    public final Queue<Record> records = new ArrayDeque<>(1000);

    public void record(double time, Vector2 pos, Vector2 heading, Vector2 vel) {
        records.add(new Record(time, pos, heading, vel));
    }

    public static class Record {
        // the time which the record was recorded at.
        public double time;

        // position vector
        public Vector2 position;

        // heading vector, for torch
        public Vector2 heading;

        // velocity, for determining animation
        public Vector2 velocity;

        public Record(double time, Vector2 position, Vector2 heading, Vector2 velocity) {
            this.time = time;
            this.position = position;
            this.heading = heading;
            this.velocity = velocity;
        }
    }
}
