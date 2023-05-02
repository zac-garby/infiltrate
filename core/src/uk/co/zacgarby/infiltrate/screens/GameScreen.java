package uk.co.zacgarby.infiltrate.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Filter;
import uk.co.zacgarby.infiltrate.Game;
import uk.co.zacgarby.infiltrate.components.graphics.*;
import uk.co.zacgarby.infiltrate.components.mechanics.*;
import uk.co.zacgarby.infiltrate.components.physical.*;
import uk.co.zacgarby.infiltrate.systems.*;

import java.util.*;

import static uk.co.zacgarby.infiltrate.etc.Map.makeMapMask;

public class GameScreen implements Screen, TaskSystem.TaskCallback {
    private final Game game;
    private final Engine engine;
    private final int levelNum;

    private final Entity player;

    public GameScreen(Game game, int levelNum, List<Queue<MovementRecorderComponent.Record>> recordings) {
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

        Filter playerFilter = new Filter();
        playerFilter.groupIndex = -1; // don't collide with other players

        engine = new Engine();

        Entity world = new Entity();
        world.add(new MapComponent(map));
        world.add(new PositionComponent(0, 0));
        world.add(new PhysicsWorldComponent(map));
        engine.addEntity(world);

        Entity gameState = new Entity();
        gameState.add(new GameStateComponent());
        engine.addEntity(gameState);

        player = new Entity();
        player.add(new PlayerComponent());
        player.add(new TextureComponent(new Texture("img/agent.png"), 9f, 11f));
        player.add(new TextureSliceComponent(0, 0, 9, 11));
        player.add(new AnimationComponent(2).set(1, 2));
        player.add(new PositionComponent(280, 340));
        player.add(new MovementComponent());
        player.add(new MovementRecorderComponent());
        player.add(new RigidbodyComponent(2f, 0f, -3.5f)
                .setFilter(playerFilter));
        player.add(new MovementControlsComponent(130f));
        player.add(new CameraFollowComponent(camera));
        engine.addEntity(player);

        for (Queue<MovementRecorderComponent.Record> records : recordings) {
            Entity oldPlayer = new Entity();
            oldPlayer.add(new TextureComponent(new Texture("img/agent.png"), 9f, 11f));
            oldPlayer.add(new TextureSliceComponent(0, 0, 9, 11));
            oldPlayer.add(new AnimationComponent(2).set(0, 0));
            oldPlayer.add(new PositionComponent(280, 340));
            oldPlayer.add(new MovementPlaybackComponent(records));
            engine.addEntity(oldPlayer);
        }

        MapLayer tasksLayer = map.getLayers().get("Tasks Level " + levelNum);
        if (tasksLayer != null) {
            Map<Integer, List<RectangleMapObject>> possibleTasks = new HashMap<>();

            for (MapObject object : tasksLayer.getObjects()) {
                Object maybeOrder = object.getProperties().get("order");

                if (object instanceof RectangleMapObject && maybeOrder != null) {
                    RectangleMapObject rectangleObject = (RectangleMapObject) object;
                    int order = (int) maybeOrder;

                    if (!possibleTasks.containsKey(order)) {
                        possibleTasks.put(order, new ArrayList<RectangleMapObject>());
                    }

                    possibleTasks.get(order).add(rectangleObject);
                }
            }

            for (int order : possibleTasks.keySet()) {
                List<RectangleMapObject> possibilities = possibleTasks.get(order);
                int r = (int) (Math.random() * possibilities.size());
                RectangleMapObject rectangleObject = possibilities.get(r);
                Rectangle rect = rectangleObject.getRectangle();

                System.out.println("registered task of order " + order + " at " + rect.x + ", " + rect.y);

                Entity task = new Entity();
                task.add(new InteractionComponent(rect.width, rect.height));
                task.add(new TaskComponent("task name #" + order, order, null));
                task.add(new TextureComponent(new Texture("img/highlight.png"), rect.width, rect.height));
                task.add(new TextureSliceComponent(0, 0, 1, 1));
                task.add(new AnimationComponent(4).set(0, 0));
                task.add(new PositionComponent(
                        rect.x + rect.width / 2,
                        rect.y + rect.height / 2));
                task.add(new HiddenComponent());

                engine.addEntity(task);
            }
        }

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
        engine.addSystem(new MovementRecordingSystem(0.1f));
        engine.addSystem(new MovementPlaybackSystem());
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
        game.addRecording(MovementRecorderComponent.mapper.get(player).records);
        game.setScreen(game.screenForLevel(levelNum + 1));
    }

    @Override
    public void onTaskComplete(TaskComponent task) {
        if (task.cutscene != null) {
            game.setScreen(new CutsceneScreen(game, this, task.cutscene));
        }
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
