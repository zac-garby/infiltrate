package uk.co.zacgarby.infiltrate.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.co.zacgarby.infiltrate.Game;
import uk.co.zacgarby.infiltrate.components.graphics.UITextureComponent;
import uk.co.zacgarby.infiltrate.systems.DialogueSystem;
import uk.co.zacgarby.infiltrate.systems.UIRenderSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class CutsceneScreen implements Screen, DialogueSystem.DialogueCallback {
    private final Engine engine;
    private final Game game;
    private final Screen returnTo;

    public CutsceneScreen(Game game, Screen returnTo, String[] lines) {
        this.game = game;
        this.returnTo = returnTo;

        ArrayList<String> linesCol = new ArrayList<>(Arrays.asList(lines));
        linesCol.add("\\");
        linesCol.add("             [ press space to continue ]");

        engine = new Engine();

        Entity profile = new Entity();
        profile.add(new UITextureComponent(new Texture("img/profile.png"), 0, 0));
        engine.addEntity(profile);

        engine.addSystem(new UIRenderSystem(game.batch, game.viewportWidth, game.viewportHeight));
        engine.addSystem(new DialogueSystem(10, 70, linesCol, this));
    }

    @Override
    public void onDialogueFinish(DialogueSystem system) {
        game.setScreen(returnTo);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.13f, 0.0667f, 0.1529f, 1f);
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
