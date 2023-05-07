package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import uk.co.zacgarby.infiltrate.Game;

public class IntroScreenSystem extends EntitySystem implements FadeSystem.Callback {
    private final Screen to;
    private final Game game;
    private FadeSystem fadeOut;

    public IntroScreenSystem(Game game, Screen to) {
        this.game = game;
        this.to = to;
        fadeOut = new FadeSystem(game, 0.8f, false, this);
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            getEngine().addSystem(fadeOut);
        }
    }

    @Override
    public void onFadeComplete(FadeSystem fade) {
        game.setScreen(to);
    }
}
