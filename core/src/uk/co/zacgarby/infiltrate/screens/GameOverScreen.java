package uk.co.zacgarby.infiltrate.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.co.zacgarby.infiltrate.Game;
import uk.co.zacgarby.infiltrate.components.graphics.UITextComponent;
import uk.co.zacgarby.infiltrate.components.graphics.UITextureComponent;
import uk.co.zacgarby.infiltrate.systems.DialogueSystem;
import uk.co.zacgarby.infiltrate.systems.IntroScreenSystem;
import uk.co.zacgarby.infiltrate.systems.UIRenderSystem;

public class GameOverScreen implements Screen {
    private final Engine engine;

    public GameOverScreen(Game game, Screen nextScreen) {
        engine = new Engine();

        Entity background = new Entity();
        background.add(new UITextureComponent(new Texture("img/outro.png"), 0, 0));
        engine.addEntity(background);

        engine.addSystem(new UIRenderSystem(game.batch, game.viewportWidth, game.viewportHeight));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.02f, 0.1067f, 0.059f, 1f);
        engine.update(delta);
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
