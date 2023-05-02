package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import uk.co.zacgarby.infiltrate.components.graphics.TextComponent;

import java.util.ArrayList;
import java.util.Collection;

public class DialogueSystem extends EntitySystem {
    public int skipKey = Input.Keys.SPACE;

    private ArrayList<String> messages;
    private ArrayList<Entity> entities = new ArrayList<>();
    private DialogueCallback callback;
    private final int originX, originY;

    public DialogueSystem(int x, int y, Collection<String> messages, DialogueCallback callback) {
        originX = x;
        originY = y;

        this.messages = new ArrayList<>(messages);
        this.callback = callback;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        nextText();
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(skipKey)) {
            nextText();
        }
    }

    private void nextText() {
        if (messages.size() == 0) {
            if (callback != null) {
                callback.onDialogueFinish(this);
            }

            getEngine().removeSystem(this);
            return;
        }

        for (Entity e : entities) {
            TextComponent t = TextComponent.mapper.get(e);
            t.y += 8;
        }

        String message = messages.remove(0);
        boolean cont = message.length() > 0 && message.charAt(message.length() - 1) == '\\';
        if (cont) {
            message = message.substring(0, message.length() - 1);
        }

        Entity text = new Entity();
        text.add(new TextComponent(message, originX, originY));
        getEngine().addEntity(text);
        entities.add(text);

        if (cont) {
            nextText();
        }
    }

    public interface DialogueCallback {
        void onDialogueFinish(DialogueSystem system);
    }
}
