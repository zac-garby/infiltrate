package uk.co.zacgarby.infiltrate.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.co.zacgarby.infiltrate.Game;
import uk.co.zacgarby.infiltrate.components.ui.UITextureComponent;
import uk.co.zacgarby.infiltrate.systems.DialogueSystem;
import uk.co.zacgarby.infiltrate.systems.FadeSystem;
import uk.co.zacgarby.infiltrate.systems.UIRenderSystem;

import java.util.ArrayList;
import java.util.Arrays;

public class CutsceneScreen implements Screen, DialogueSystem.DialogueCallback, FadeSystem.Callback {
    private final Engine engine;
    private final Game game;
    private final Screen returnTo;
    private final boolean playMusic;
    private final Music music;

    public CutsceneScreen(Game game, Screen returnTo, String[] lines, boolean playMusic) {
        this.game = game;
        this.returnTo = returnTo;
        this.playMusic = playMusic;

        music = Gdx.audio.newMusic(Gdx.files.internal("music/cutscene.wav"));
        music.setLooping(true);

        ArrayList<String> linesCol = new ArrayList<>(Arrays.asList(lines));
        linesCol.add("\\");
        linesCol.add("             [ press space to continue ]");

        engine = new Engine();

        Entity profile = new Entity();
        profile.add(new UITextureComponent(new Texture("img/profile.png"), 0, 0));
        engine.addEntity(profile);

        engine.addSystem(new UIRenderSystem(game.batch, game.viewportWidth, game.viewportHeight));
        engine.addSystem(new DialogueSystem(10, 70, linesCol, this));
        engine.addSystem(new FadeSystem(game, 0.2f, true));
    }

    public CutsceneScreen(Game game, Screen returnTo, String[] lines) {
        this(game, returnTo, lines, false);
    }

    @Override
    public void onDialogueFinish(DialogueSystem system) {
        engine.addSystem(new FadeSystem(game, 0.2f, false, this));
    }

    @Override
    public void onFadeComplete(FadeSystem fade) {
        game.setScreen(returnTo);
    }

    @Override
    public void show() {
        if (playMusic) {
            game.musicPlayer.queue(music, (60f / 140f) * 8f);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.6f * 0.13f, 0.6f * 0.0667f, 0.6f * 0.1529f, 1f);
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
