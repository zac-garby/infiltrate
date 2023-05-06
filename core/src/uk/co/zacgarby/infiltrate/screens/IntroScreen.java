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

public class IntroScreen implements Screen {
    private final Engine engine;

    public IntroScreen(Game game, Screen firstLevel) {
        engine = new Engine();

        Entity background = new Entity();
        background.add(new UITextureComponent(new Texture("img/intro.png"), 0, 0));
        background.add(new UITextComponent(
                "[ press the any key to start ]", 100, 140,
                UITextComponent.Align.Center,
                UITextComponent.Effect.Flashing));
        engine.addEntity(background);

        engine.addSystem(new UIRenderSystem(game.batch, game.viewportWidth, game.viewportHeight));
        engine.addSystem(new IntroScreenSystem(game, firstLevel));
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
