package uk.co.zacgarby.infiltrate.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import uk.co.zacgarby.infiltrate.Game;
import uk.co.zacgarby.infiltrate.components.graphics.*;
import uk.co.zacgarby.infiltrate.components.mechanics.*;
import uk.co.zacgarby.infiltrate.components.physical.*;
import uk.co.zacgarby.infiltrate.systems.*;

import static uk.co.zacgarby.infiltrate.etc.Map.makeMapMask;

public class GameScreen implements Screen, TaskSystem.TaskCallback {
    private final Game game;
    private final Engine engine;
    private final int levelNum;

    public GameScreen(Game game, int levelNum) {
        this.levelNum = levelNum;
        this.game = game;

        OrthographicCamera camera = new OrthographicCamera(game.viewportWidth, game.viewportHeight);

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

        Entity gameState = new Entity();
        gameState.add(new GameStateComponent());
        engine.addEntity(gameState);

        Entity player = new Entity();
        player.add(new PlayerComponent());
        player.add(new TextureComponent(new Texture("img/agent.png"), 9f, 11f));
        player.add(new TextureSliceComponent(0, 0, 9, 11));
        player.add(new AnimationComponent(2).set(1, 2));
        player.add(new PositionComponent(280, 340));
        player.add(new MovementComponent());
        player.add(new RigidbodyComponent(2f, 0f, -3.5f));
        player.add(new MovementControlsComponent(130f));
        player.add(new CameraFollowComponent(camera));
        engine.addEntity(player);

        Entity task = new Entity();
        task.add(new InteractionComponent(12.0f, 12.0f));
        task.add(new TaskComponent("find the secret docs.", 0));
        task.add(new TextureComponent(new Texture("img/highlight.png"), 12f, 12f));
        task.add(new TextureSliceComponent(0, 0, 1, 1));
        task.add(new AnimationComponent(4).set(0, 0));
        task.add(new PositionComponent(38 * 12 + 6, 25 * 12 + 6));
        task.add(new HiddenComponent());
        engine.addEntity(task);

        Entity task2 = new Entity();
        task2.add(new InteractionComponent(12.0f, 12.0f));
        task2.add(new TaskComponent("escape.", 1));
        task2.add(new TextureComponent(new Texture("img/highlight.png"), 12f, 12f));
        task2.add(new TextureSliceComponent(0, 0, 1, 1));
        task2.add(new AnimationComponent(4).set(0, 0));
        task2.add(new PositionComponent(40 * 12 + 6, 25 * 12 + 6));
        task2.add(new HiddenComponent());
        engine.addEntity(task2);

        Entity locationText = new Entity();
        locationText.add(new TextComponent("", 20, 183));
        locationText.add(new GPSComponent());
        engine.addEntity(locationText);

        Entity levelText = new Entity();
        levelText.add(new TextComponent("LEVEL " + levelNum, 183, 183, false));
        engine.addEntity(levelText);

        Entity taskText = new Entity();
        taskText.add(new TextComponent("* .", 20, 11));
        taskText.add(new TaskDescriptionComponent());
        engine.addEntity(taskText);

        engine.addSystem(new GameRenderSystem(game.batch, camera, lightingShader, map, mapMask));
        engine.addSystem(new UIRenderSystem(game.batch, camera.viewportWidth, camera.viewportHeight));
        engine.addSystem(new InputSystem());
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new AnimationSystem(0.1f));
        engine.addSystem(new CameraFollowSystem());
        engine.addSystem(new InteractionSystem());
        engine.addSystem(new GPSSystem(map));
        engine.addSystem(new TaskSystem(this));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float dt) {
        engine.update(dt);
    }

    @Override
    public void onAllTasksComplete() {
        game.setScreen(game.screenForLevel(levelNum + 1));
    }

    @Override
    public void onTaskComplete(TaskComponent task) {

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
