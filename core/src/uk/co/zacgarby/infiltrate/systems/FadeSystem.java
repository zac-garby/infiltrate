package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class FadeSystem extends EntitySystem {
    private final ShaderProgram fadeShader;
    private final float timeout;
    private final boolean in;
    private final Callback callback;
    private float time = 0.0f;

    public FadeSystem(ShaderProgram fadeShader, float timeout, boolean in, Callback callback) {
        this.fadeShader = fadeShader;
        this.timeout = timeout;
        this.in = in;
        this.callback = callback;
    }

    public FadeSystem(ShaderProgram fadeShader, float timeout, boolean in) {
        this(fadeShader, timeout, in, null);
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;

        if (in) {
            fadeShader.setUniformf("u_fade", Math.min(time / timeout, 1.0f));
        } else {
            fadeShader.setUniformf("u_fade", 1.0f - Math.min(time / timeout, 1.0f));
        }

        if (time > timeout) {
            getEngine().removeSystem(this);
        }
    }

    @Override
    public void removedFromEngine(Engine engine) {
        fadeShader.setUniformf("u_fade", 1.0f);

        if (callback != null) {
            callback.onFadeComplete(this);
        }
    }

    public interface Callback {
        void onFadeComplete(FadeSystem fade);
    }
}
