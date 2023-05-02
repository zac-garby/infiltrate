package uk.co.zacgarby.infiltrate.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.co.zacgarby.infiltrate.Game;
import uk.co.zacgarby.infiltrate.components.graphics.TextComponent;
import uk.co.zacgarby.infiltrate.systems.DialogueSystem;
import uk.co.zacgarby.infiltrate.systems.UIRenderSystem;

import java.util.Arrays;

public class IntroScreen implements Screen, DialogueSystem.DialogueCallback {
    private final Engine engine;
    private final Game game;

    public IntroScreen(Game game) {
        this.game = game;

        // the amount of "pixels" in the x-axis of the screen
        float scale = 200f;

        engine = new Engine();

        engine.addSystem(new UIRenderSystem(game.batch, game.viewportWidth, game.viewportHeight));
        engine.addSystem(new DialogueSystem(10, 70, Arrays.asList(
                "X-OS v1.29                          [year 2137, 19:44]\\",
                "mail\\",
                "\\",
                "                    * new mail *\\",
                "             press [space] to continue",
                "\\",
                "good evening, agent.",
                "\\",
                "CompuTronics Corp Inc. has a task for you.",
                "...",
                "sorry, uh -",
                "apparently you're not supposed to know who\\",
                "  your employer is.",
                "please ignore that.",
                "it's ok, at least you don't know my name.",
                "...",
                "anyway.",
                "we have intel that B.E.L. labs are up to no good.",
                "\\",
                "B.E.L. labs? you know them?",
                "\"big evil labs\" - nasty guys.",
                "they're based in the outskirts of the city.",
                "\\",
                "big-pharma, of course. they're doing something\\",
                "  with humans. something bad.",
                "\\",
                "naturally, we want to stop them. our business\\",
                "  model relies on people buying our products,\\",
                "  so we can't have them all eliminated.",
                "",
                "...",
                "and also, of course, benevolent reasons. yes.",
                "...",
                "\\",
                "so, right - your task!",
                "you shouldn't let me ramble on like that.",
                "you know you can skip these messages by\\",
                "  pressing space though, right?",
                "\\",
                "we'll drop you by the building, but you're\\",
                "  going to have to find a way in yourself.",
                "\\",
                "now pay attention, i'm only going to say\\",
                "  this once.\\",
                "...",
                "\\",
                "in the west wing of the building, there\\",
                "  are some offices.",
                "only minor employees - pawns - but they may\\",
                "  have some useful information.",
                "go, grab some documents from them, and bring\\",
                "  them back to us. we'll meet you in the car\\",
                "  park for extraction and... payment.",
                "\\",
                "and, feel free to explore the labs. it's a\\",
                "  cool place, but don't get lost. and don't\\",
                "  waste time. this is urgent, agent.",
                "\\",
                "the building should be empty at this time,\\",
                "  but watch out anyway.",
                "\\",
                "  best,\\",
                "  dave xoxo",
                "\\",
                "      wait,",
                "        darn. ignore that too",
                "\\",
                "\\",
                "              [ press space to begin ]"
        ), this));
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
    public void onDialogueFinish(DialogueSystem system) {
        game.setScreen(game.screenForLevel(1));
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
