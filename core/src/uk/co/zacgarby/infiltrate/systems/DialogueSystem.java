package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import uk.co.zacgarby.infiltrate.components.ui.UITextComponent;

import java.util.ArrayList;
import java.util.Collection;

public class DialogueSystem extends EntitySystem {
    public int skipKey = Input.Keys.SPACE;
    public int skipAllKey = Input.Keys.ESCAPE;

    private ArrayList<String> messages;
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final DialogueCallback callback;
    private final int originX, originY;

    private float timeout;
    private final float interval;

    public DialogueSystem(int x, int y, Collection<String> messages, DialogueCallback callback) {
        originX = x;
        originY = y;

        this.messages = new ArrayList<>(messages);
        this.callback = callback;

        this.interval = 2f;
        this.timeout = interval;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        nextText();
    }

    @Override
    public void update(float deltaTime) {
        timeout -= deltaTime;

        if (messages.size() > 0 && (timeout <= 0 || Gdx.input.isKeyJustPressed(skipKey))) {
            timeout = interval;
            nextText();
        }

        if (Gdx.input.isKeyJustPressed(skipAllKey) && messages.size() > 0) {
            while (messages.size() > 0) {
                nextText();
            }
        } else if ((Gdx.input.isKeyJustPressed(skipKey) || Gdx.input.isKeyJustPressed(skipAllKey)) && messages.size() == 0) {
            if (callback != null) {
                callback.onDialogueFinish(this);
            }

            getEngine().removeSystem(this);
        }
    }

    private void nextText() {
        if (messages.size() == 0) return;

        for (Entity e : entities) {
            UITextComponent t = UITextComponent.mapper.get(e);
            t.y += 8;
        }

        String message = messages.remove(0);
        boolean cont = message.length() > 0 && message.charAt(message.length() - 1) == '\\';
        if (cont) {
            message = message.substring(0, message.length() - 1);
        }

        Entity text = new Entity();
        text.add(new UITextComponent(
                message, originX, originY,
                UITextComponent.Align.Left, UITextComponent.Effect.TypeOut)
        );
        getEngine().addEntity(text);
        entities.add(text);

        if (cont) {
            nextText();
            timeout += interval;
        }
    }

    public interface DialogueCallback {
        void onDialogueFinish(DialogueSystem system);
    }
}
