package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import jdk.javadoc.internal.doclets.formats.html.markup.Head;
import uk.co.zacgarby.infiltrate.components.mechanics.PlayerComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.TorchComponent;
import uk.co.zacgarby.infiltrate.components.physical.HeadingComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;

public class TorchDetectionSystem extends IntervalIteratingSystem {
    public static final float RANGE = 5.0f * 12f;
    public static final float STEP_SIZE = 0.1f;
    public static final float TORCH_ANGLE = 50.0f; // degrees
    private final Entity player;
    private final Pixmap mapMask;
    private final DetectionListener listener;

    public TorchDetectionSystem(float interval, Texture mapMask, Entity player, DetectionListener listener) {
        super(Family
                .all(
                        TorchComponent.class,
                        PositionComponent.class,
                        HeadingComponent.class
                )
                .exclude(
                        PlayerComponent.class
                ).get(),
                interval);

        this.player = player;
        this.listener = listener;

        if (!mapMask.getTextureData().isPrepared()) {
            mapMask.getTextureData().prepare();
        }

        this.mapMask = mapMask.getTextureData().consumePixmap();
    }

    @Override
    protected void processEntity(Entity entity) {
        Vector2 playerPos = PositionComponent.mapper.get(player).position.cpy();
        Vector2 torchPos = PositionComponent.mapper.get(entity).position.cpy();
        Vector2 heading = HeadingComponent.mapper.get(entity).heading;

        if (canSee(torchPos, heading, playerPos)) {
            listener.onDetected(entity);
        }
    }

    public boolean canSee(Vector2 from, Vector2 heading, Vector2 to) {
        Vector2 diff = to.sub(from);

        if (diff.len() >= RANGE) {
            return false;
        }

        int steps = (int) (diff.len() / STEP_SIZE);
        Vector2 delta = diff.cpy().nor().scl(STEP_SIZE);

        for (int i = 0; i < steps; i++) {
            from.add(delta);
            int pix = mapMask.getPixel((int) from.x, mapMask.getHeight() - (int) from.y);

            if (pix > 0) {
                return false;
            }
        }

        float d = heading.dot(diff.add(heading.cpy().scl(0.5f)).nor());
        float a = MathUtils.radDeg * MathUtils.acos(d);

        return a <= TORCH_ANGLE;
    }

    public interface DetectionListener {
        void onDetected(Entity detectedBy);
    }
}
