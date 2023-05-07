package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import uk.co.zacgarby.infiltrate.Game;

public class FadeSystem extends EntitySystem {
    private final Game game;
    private final float timeout;
    private final boolean in;
    private final Callback callback;
    private float time = 0.0f;
    private boolean done = false;

    public FadeSystem(Game game, float timeout, boolean in, Callback callback) {
        this.game = game;
        this.timeout = timeout;
        this.in = in;
        this.callback = callback;
    }

    public FadeSystem(Game fadeShader, float timeout, boolean in) {
        this(fadeShader, timeout, in, null);
    }

    @Override
    public void update(float deltaTime) {
        if (done) {
            return;
        }

        time += deltaTime;
        float fade;

        if (in) {
            fade = Math.min(time / timeout, 1.0f);
        } else {
            fade = 1.0f - Math.min(time / timeout, 1.0f);
        }

        game.fboShader.setUniformf("u_fade", fade);

        if (time > timeout) {
            done = true;

            if (callback != null) {
                callback.onFadeComplete(this);
                getEngine().removeSystem(this);
            }
        }
    }

    public interface Callback {
        void onFadeComplete(FadeSystem fade);
    }
}
