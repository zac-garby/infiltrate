package uk.co.zacgarby.infiltrate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import uk.co.zacgarby.infiltrate.screens.GameScreen;
import uk.co.zacgarby.infiltrate.screens.IntroScreen;

public class Game extends com.badlogic.gdx.Game {
	public SpriteBatch batch;
	public float viewportWidth, viewportHeight;

	@Override
	public void create () {
		float scale = 200f;
		viewportWidth = scale;
		viewportHeight =  scale * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth());

		batch = new SpriteBatch();

		this.setScreen(new IntroScreen(this));
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
