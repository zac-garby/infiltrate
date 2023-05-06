package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.Screen;
import uk.co.zacgarby.infiltrate.Game;

public class IntroScreenSystem extends IntervalSystem {
    private Screen to;
    private Game game;

    public IntroScreenSystem(Game game, Screen to, float interval) {
        super(interval);
        this.game = game;
        this.to = to;
    }

    @Override
    protected void updateInterval() {
        game.setScreen(to);
    }
}
