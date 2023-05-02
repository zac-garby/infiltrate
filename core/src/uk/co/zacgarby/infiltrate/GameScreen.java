package uk.co.zacgarby.infiltrate;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import uk.co.zacgarby.infiltrate.components.*;
import uk.co.zacgarby.infiltrate.systems.*;

import static uk.co.zacgarby.infiltrate.Map.makeMapMask;

public class GameScreen implements Screen {
    private final Game game;
    private final Engine engine;
    private final OrthographicCamera camera;

    private final Box2DDebugRenderer box2DDebugRenderer;
    private final World worldForDebug;

    public GameScreen(Game game) {
        this.game = game;

        // the amount of "pixels" in the x-axis of the screen
        float scale = 200f;

        camera = new OrthographicCamera(
                scale,
                scale * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));

        if (Math.floor(camera.viewportWidth) != camera.viewportWidth) {
            throw new RuntimeException("non-integer viewport width");
        } else if (Math.floor(camera.viewportHeight) != camera.viewportHeight) {
            throw new RuntimeException("non-integer viewport height");
        }

        ShaderProgram lightingShader = new ShaderProgram(
                Gdx.files.internal("shaders/lighting.vert"),
                Gdx.files.internal("shaders/lighting.frag"));

        TiledMap map = new TmxMapLoader().load("map12.tmx");
        Texture mapMask = makeMapMask(map, new Pixmap(Gdx.files.internal("img/tileset-mask.png")));

        engine = new Engine();

        Entity world = new Entity();
        world.add(new MapComponent(map));
        world.add(new PositionComponent(0, 0));
        world.add(new PhysicsWorldComponent(map));
        engine.addEntity(world);

        engine.addSystem(new RenderSystem(game.batch, camera, lightingShader, map, mapMask));
        engine.addSystem(new InputSystem());
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new AnimationSystem(0.1f));
        engine.addSystem(new CameraFollowSystem());
        engine.addSystem(new InteractionSystem());
        engine.addSystem(new GPSSystem(map));

        Entity player = new Entity();
        player.add(new PlayerComponent());
        player.add(new TextureComponent(new Texture("img/agent.png"), 9f, 11f));
        player.add(new TextureSliceComponent(0, 0, 9, 11));
        player.add(new AnimationComponent(2).set(1, 2));
        player.add(new PositionComponent(280, 340));
        player.add(new MovementComponent());
        player.add(new RigidbodyComponent(2f, 0f, -3.5f));
        player.add(new MovementControlsComponent(200f));
        player.add(new CameraFollowComponent(camera));
        engine.addEntity(player);

        Entity task = new Entity();
        task.add(new InteractionComponent(36.0f, 12.0f));
        task.add(new TaskComponent("find the secret docs."));
        task.add(new TextureComponent(new Texture("img/highlight.png"), 36f, 12f));
        task.add(new TextureSliceComponent(0, 0, 1, 1));
        task.add(new AnimationComponent(4).set(0, 0));
        task.add(new PositionComponent(38 * 12 + 6, 25 * 12 + 6));
        engine.addEntity(task);

        Entity locationText = new Entity();
        locationText.add(new TextComponent("", 20, 183));
        locationText.add(new GPSComponent());
        engine.addEntity(locationText);

        Entity levelText = new Entity();
        levelText.add(new TextComponent("LEVEL 1", 183, 183, false));
        engine.addEntity(levelText);

        Entity taskText = new Entity();
        taskText.add(new TextComponent("* FIND THE SECRET DOCUMENTS.", 20, 11));
        engine.addEntity(taskText);

        box2DDebugRenderer = new Box2DDebugRenderer();
        worldForDebug = PhysicsWorldComponent.mapper.get(world).world;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float dt) {
        engine.update(dt);
//        box2DDebugRenderer.render(worldForDebug, camera.combined);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
