package uk.co.zacgarby.infiltrate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import uk.co.zacgarby.infiltrate.screens.GameScreen;

public class Game extends com.badlogic.gdx.Game {
	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		this.setScreen(new GameScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
