package uk.co.zacgarby.infiltrate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementRecorderComponent;
import uk.co.zacgarby.infiltrate.screens.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Game extends com.badlogic.gdx.Game {
	public SpriteBatch batch;
	public FrameBuffer fbo;
	private TextureRegion fboRegion;
	private Matrix4 idMatrix;
	public ShaderProgram fboShader;
	public float fade = 0.0f;

	public float viewportWidth, viewportHeight;
	private final List<Queue<MovementRecorderComponent.Record>> previousRecordings = new ArrayList<>(5);
	public TiledMap map;

	@Override
	public void create () {
		float scale = 200f;
		viewportWidth = scale;
		viewportHeight = scale * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth());

		batch = new SpriteBatch();

		fboShader = new ShaderProgram(
				Gdx.files.internal("shaders/fade.vert"),
				Gdx.files.internal("shaders/fade.frag")
		);

		fbo = new FrameBuffer(Pixmap.Format.RGB888, (int) viewportWidth, (int) viewportHeight, false);

		Texture destFBOTex = fbo.getColorBufferTexture();
		destFBOTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		fboRegion = new TextureRegion(destFBOTex);
		fboRegion.flip(false, true);

		OrthographicCamera idCamera = new OrthographicCamera(viewportWidth, viewportHeight);
		idCamera.translate(viewportWidth / 2, viewportHeight / 2);
		idCamera.update();
		idMatrix = idCamera.combined;

		map = new TmxMapLoader().load("map12.tmx");

		Screen firstLevel = screenForLevel(1);
		this.setScreen(new IntroScreen(this, firstLevel));
	}

	@Override
	public void render () {
		fboShader.setUniformf("u_fade", fade);

		// render screen to the down-scaled FBO
		fbo.begin();
		ScreenUtils.clear(0, 0, 0, 1);
		super.render();
		fbo.end();

		// render FBO to the actual screen
		batch.setProjectionMatrix(idMatrix);
		batch.setShader(fboShader);
		batch.begin();
		batch.draw(fboRegion, 0, 0, viewportWidth, viewportHeight);
		batch.end();
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
