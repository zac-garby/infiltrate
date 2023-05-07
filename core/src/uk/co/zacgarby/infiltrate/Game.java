package uk.co.zacgarby.infiltrate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementRecorderComponent;
import uk.co.zacgarby.infiltrate.screens.CutsceneScreen;
import uk.co.zacgarby.infiltrate.screens.GameOverScreen;
import uk.co.zacgarby.infiltrate.screens.GameScreen;
import uk.co.zacgarby.infiltrate.screens.IntroScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Game extends com.badlogic.gdx.Game {
	public SpriteBatch batch;
	public float viewportWidth, viewportHeight;
	private final List<Queue<MovementRecorderComponent.Record>> previousRecordings = new ArrayList<>(5);
	public TiledMap map;

	@Override
	public void create () {
		float scale = 200f;
		viewportWidth = scale;
		viewportHeight =  scale * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth());

		batch = new SpriteBatch();
		map = new TmxMapLoader().load("map12.tmx");

		this.setScreen(new IntroScreen(this, screenForLevel(1)));
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
		if (level <= 5) {
			MapLayer spawnsLayer = map.getLayers().get("Spawnpoints");
			RectangleMapObject spawn = (RectangleMapObject) spawnsLayer.getObjects().get("Spawn " + level);
			String[] cutscene = spawn.getProperties().get("cutscene", String.class).split("\n");

			return new CutsceneScreen(
					this,
					new GameScreen(this, level, previousRecordings),
					cutscene);
		} else {
			return new GameOverScreen(this, null);
		}
	}
}
