package uk.co.zacgarby.infiltrate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementRecorderComponent;
import uk.co.zacgarby.infiltrate.screens.GameScreen;
import uk.co.zacgarby.infiltrate.screens.IntroScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Game extends com.badlogic.gdx.Game {
	public SpriteBatch batch;
	public float viewportWidth, viewportHeight;
	private List<Queue<MovementRecorderComponent.Record>> previousRecordings = new ArrayList<>(5);

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

	public void addRecording(Queue<MovementRecorderComponent.Record> records) {
		previousRecordings.add(records);
	}

	public Screen screenForLevel(int level) {
		return new GameScreen(this, level, previousRecordings);
	}
}
