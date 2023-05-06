package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import uk.co.zacgarby.infiltrate.Game;

public class IntroScreenSystem extends EntitySystem {
    private final Screen to;
    private final Game game;

    public IntroScreenSystem(Game game, Screen to) {
        this.game = game;
        this.to = to;
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(to);
        }
    }
}
