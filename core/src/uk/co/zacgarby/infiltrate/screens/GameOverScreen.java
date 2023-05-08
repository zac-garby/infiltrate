package uk.co.zacgarby.infiltrate.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.co.zacgarby.infiltrate.Game;
import uk.co.zacgarby.infiltrate.components.ui.UITextComponent;
import uk.co.zacgarby.infiltrate.components.ui.UITextureComponent;
import uk.co.zacgarby.infiltrate.systems.ClockSystem;
import uk.co.zacgarby.infiltrate.systems.FadeSystem;
import uk.co.zacgarby.infiltrate.systems.UIRenderSystem;

public class GameOverScreen implements Screen, FadeSystem.Callback {
    private final Engine engine;
    private final float time;
    private final Game game;
    private final Screen nextScreen;
    private final Music music;

    public GameOverScreen(Game game, Screen nextScreen, float time) {
        this.game = game;
        this.nextScreen = nextScreen;
        engine = new Engine();
        this.time = time;

        music = Gdx.audio.newMusic(Gdx.files.internal("music/gameover.wav"));

        Entity background = new Entity();
        background.add(new UITextureComponent(new Texture("img/outro.png"), 0, 0));
        engine.addEntity(background);

        Entity timeText = new Entity();
        timeText.add(new UITextComponent(
                "you destroyed the world...",
                100, 160,
                UITextComponent.Align.Center,
                null,
                new Color(1.0f, 0.7f, 0.8f, 1.0f)
        ));
        engine.addEntity(timeText);

        engine.addSystem(new UIRenderSystem(game.batch, game.viewportWidth, game.viewportHeight));
        engine.addSystem(new FadeSystem(game, 10.0f, true, this));
    }

    @Override
    public void onFadeComplete(FadeSystem fade) {
        if (fade.in) {
            Entity timeText = new Entity();
            timeText.add(new UITextComponent(
                    "in just " + ClockSystem.getTimeString(time) + " - well done!",
                    100, 150,
                    UITextComponent.Align.Center
            ));
            engine.addEntity(timeText);

            // fade out
            engine.addSystem(new FadeSystem(game, 10.0f, false, this));
        } else {
            game.setScreen(nextScreen);
        }
    }

    @Override
    public void show() {
        game.musicPlayer.queue(music, 20.0f);
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
