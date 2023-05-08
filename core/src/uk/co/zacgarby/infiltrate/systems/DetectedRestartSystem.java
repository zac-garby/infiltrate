package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import uk.co.zacgarby.infiltrate.Game;

public class DetectedRestartSystem extends EntitySystem {
    private final Screen screen;
    private final Game game;

    public DetectedRestartSystem(Game game, Screen returnTo) {
        this.game = game;
        screen = returnTo;
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(screen);
        }
    }
}
