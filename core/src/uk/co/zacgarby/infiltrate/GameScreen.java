package uk.co.zacgarby.infiltrate;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import uk.co.zacgarby.infiltrate.components.*;
import uk.co.zacgarby.infiltrate.systems.*;

public class GameScreen implements Screen {
    private final Game game;
    private final Texture img;
    private final Engine engine;

    public GameScreen(Game game) {
        this.game = game;

        // the amount of "pixels" in the x-axis of the screen
        float scale = 180f;

        OrthographicCamera camera = new OrthographicCamera(
                scale,
                scale * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));

        img = new Texture("test.png");

        engine = new Engine();
        engine.addSystem(new RenderSystem(game.batch, camera));
        engine.addSystem(new InputSystem());
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new AnimationSystem(0.1f));
        engine.addSystem(new CameraFollowSystem());

        Entity entity = new Entity();
        entity.add(new TextureComponent(img, 180f, 180f));
        entity.add(new PositionComponent(0, 0));
        engine.addEntity(entity);

        Entity entity2 = new Entity();
        entity2.add(new TextureComponent(new Texture("agent.png"), 9f, 11f));
        entity2.add(new TextureSliceComponent(0, 0, 9, 11));
        entity2.add(new AnimationComponent(1).set(1, 2));
        entity2.add(new PositionComponent(0, 0));
        entity2.add(new MovementComponent());
        entity2.add(new MovementControlsComponent(25f));
        entity2.add(new CameraFollowComponent(camera));
        engine.addEntity(entity2);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float dt) {
        engine.update(dt);
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
        img.dispose();
    }
}
