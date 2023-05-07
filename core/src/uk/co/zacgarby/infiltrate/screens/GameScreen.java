package uk.co.zacgarby.infiltrate.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import uk.co.zacgarby.infiltrate.Game;
import uk.co.zacgarby.infiltrate.components.ui.*;
import uk.co.zacgarby.infiltrate.components.graphics.*;
import uk.co.zacgarby.infiltrate.components.mechanics.*;
import uk.co.zacgarby.infiltrate.components.physical.*;
import uk.co.zacgarby.infiltrate.systems.*;

import java.util.*;

import static uk.co.zacgarby.infiltrate.etc.Map.makeMapMask;

public class GameScreen implements Screen, TaskSystem.TaskCallback, TorchDetectionSystem.DetectionListener, FadeSystem.Callback {
    private final Game game;
    private final Engine engine;
    private final int levelNum;

    private final Entity player;

    public GameScreen(Game game, int levelNum, float time, List<Queue<MovementRecorderComponent.Record>> recordings) {
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

        TiledMap map = game.map;

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

        player = makePlayer(camera, map, levelNum);
        loadRecordedPlayers(recordings);
        loadTasks(levelNum, map);
        makeUI(levelNum);

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
        engine.addSystem(new TorchDetectionSystem(0.1f, mapMask, player, this));
        engine.addSystem(new FadeSystem(game, 0.3f, true));
        engine.addSystem(new TracerSystem(player));
        engine.addSystem(new ClockSystem(time));
    }

    private void makeUI(int levelNum) {
        Entity locationText = new Entity();
        locationText.add(new UITextComponent("", 20, 183));
        locationText.add(new GPSComponent());
        engine.addEntity(locationText);

        Entity levelText = new Entity();
        levelText.add(new UITextComponent("LEVEL " + levelNum, 183, 183, UITextComponent.Align.Right));
        engine.addEntity(levelText);

        Entity timeText = new Entity();
        timeText.add(new UITextComponent("00:00", 183, 190, UITextComponent.Align.Right));
        timeText.add(new ClockComponent());
        engine.addEntity(timeText);

        Entity taskText = new Entity();
        taskText.add(new UITextComponent("* .", 20, 12));
        taskText.add(new TaskDescriptionComponent());
        engine.addEntity(taskText);

        Entity taskLocationText = new Entity();
        taskLocationText.add(new UITextComponent(
                "[go to: ...]",
                20, 5,
                UITextComponent.Align.Left,
                null,
                new Color(0.5f, 0.4f, 0.4f, 1.0f)));
        taskLocationText.add(new InstructionTextComponent());
        engine.addEntity(taskLocationText);
    }

    private void loadRecordedPlayers(List<Queue<MovementRecorderComponent.Record>> recordings) {
        for (Queue<MovementRecorderComponent.Record> records : recordings) {
            Entity oldPlayer = new Entity();
            oldPlayer.add(new TextureComponent(new Texture("img/agent.png"), 9f, 11f));
            oldPlayer.add(new TextureSliceComponent(0, 0, 9, 11));
            oldPlayer.add(new AnimationComponent(2).set(0, 0));
            oldPlayer.add(new PositionComponent(280, 340));
            oldPlayer.add(new HeadingComponent(new Vector2(0.0f, 0.0f)));
            oldPlayer.add(new MovementPlaybackComponent(new ArrayDeque<>(records)));
            oldPlayer.add(new TorchComponent());
            engine.addEntity(oldPlayer);

            Entity tracer = new Entity();
            tracer.add(new UITextComponent(
                    "!", -10, -10,
                    UITextComponent.Align.Center,
                    UITextComponent.Effect.Flashing,
                    Color.RED));
            tracer.add(new TracerComponent(oldPlayer));
//            tracer.add(new HiddenComponent());
            engine.addEntity(tracer);
        }
    }

    private Entity makePlayer(OrthographicCamera camera, TiledMap map, int levelNum) {
        MapLayer spawnLayer = map.getLayers().get("Spawnpoints");
        RectangleMapObject spawn = (RectangleMapObject) spawnLayer.getObjects().get("Spawn " + levelNum);

        Filter playerFilter = new Filter();
        playerFilter.groupIndex = -1; // don't collide with other players

        final Entity player = new Entity();
        player.add(new PlayerComponent());
        player.add(new TextureComponent(new Texture("img/agent.png"), 9f, 11f));
        player.add(new TextureSliceComponent(0, 0, 9, 11));
        player.add(new AnimationComponent(2).set(1, 2));
        player.add(new PositionComponent(spawn.getRectangle().x, spawn.getRectangle().y));
        player.add(new MovementComponent());
        player.add(new MovementRecorderComponent());
        player.add(new RigidbodyComponent(2f, 0f, -3.5f)
                .setFilter(playerFilter));
        player.add(new MovementControlsComponent(160f));
        player.add(new CameraFollowComponent(camera));
        player.add(new HeadingComponent(new Vector2(1.0f, 0.0f)));
        player.add(new TorchComponent());
        engine.addEntity(player);

        return player;
    }

    private void loadTasks(int levelNum, TiledMap map) {
        Random rand = new Random();

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
                int r = Math.abs(rand.nextInt()) % possibilities.size();
                RectangleMapObject rectangleObject = possibilities.get(r);
                Rectangle rect = rectangleObject.getRectangle();
                MapProperties properties = rectangleObject.getProperties();

                String[] cutscene = null;
                if (properties.containsKey("cutscene")) {
                    String cutsceneString = properties.get("cutscene", String.class);
                    cutscene = cutsceneString.split("\n");
                }

                Entity task = new Entity();
                task.add(new InteractionComponent(rect.width, rect.height));
                task.add(new TaskComponent(
                        properties.get("description", String.class),
                        order, cutscene));
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
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float dt) {
        engine.update(dt);
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            onAllTasksComplete();
        }
    }

    @Override
    public void onAllTasksComplete() {
        removeSystemsAndUI();

        engine.addSystem(new FadeSystem(game, 0.5f, false, this));
    }

    @Override
    public void onFadeComplete(FadeSystem fade) {
        game.addRecording(MovementRecorderComponent.mapper.get(player).records);
        game.setScreen(game.screenForLevel(levelNum + 1, getTimer()));
    }

    @Override
    public void onTaskComplete(TaskComponent task) {
        if (task.cutscene != null) {
            game.setScreen(new CutsceneScreen(game, this, task.cutscene));
        }
    }

    @Override
    public void onDetected(Entity detectedBy) {
        removeSystemsAndUI();

        Entity text = new Entity();
        text.add(new PositionComponent(100, 100));
        text.add(new UITextComponent("!! you were detected !!", 61, 103));
        engine.addEntity(text);

        Entity text2 = new Entity();
        text2.add(new PositionComponent(100, 100));
        text2.add(new UITextComponent("press [ENTER] to try again...", 52, 95));
        engine.addEntity(text2);

        engine.addSystem(new GameOverSystem(game, game.screenForLevel(levelNum, getTimer())));
        engine.getSystem(GameRenderSystem.class).gameOver = true;
    }

    private void removeSystemsAndUI() {
        engine.removeSystem(engine.getSystem(InputSystem.class));
        engine.removeSystem(engine.getSystem(PhysicsSystem.class));
        engine.removeSystem(engine.getSystem(AnimationSystem.class));
        engine.removeSystem(engine.getSystem(InteractionSystem.class));
        engine.removeSystem(engine.getSystem(TaskSystem.class));
        engine.removeSystem(engine.getSystem(MovementRecordingSystem.class));
        engine.removeSystem(engine.getSystem(MovementPlaybackSystem.class));
        engine.removeSystem(engine.getSystem(TorchDetectionSystem.class));

        engine.removeAllEntities(Family.all(UITextComponent.class).get());
    }

    public float getTimer() {
        ClockSystem clock = engine.getSystem(ClockSystem.class);
        return clock.time;
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
