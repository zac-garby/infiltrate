package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import uk.co.zacgarby.infiltrate.components.ui.UITextureComponent;
import uk.co.zacgarby.infiltrate.etc.Font;
import uk.co.zacgarby.infiltrate.components.ui.UITextComponent;

public class UIRenderSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final Font font;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;
    private float flashTime = 0.0f, typeTime = 0.0f;
    private final float flashInterval = 0.35f, typeInterval = 0.02f;
    private boolean flashOn = true;

    public UIRenderSystem(SpriteBatch batch, float viewportWidth, float viewportHeight) {
        super(Family.one(UITextComponent.class, UITextureComponent.class).get(), 1100);
        this.batch = batch;

        shader = batch.getShader();

        font = new Font(
                Gdx.files.internal("img/font.png"),
                "abcdefghijklmnopqrstuvwxyz0123456789.,()[]{}<>/\\|!-_+=;:?'\"%#~*"
        );

        camera = new OrthographicCamera(viewportWidth, viewportHeight);
        camera.translate(viewportWidth / 2, viewportHeight / 2);
        camera.update();
    }

    @Override
    protected void processEntity(Entity e, float deltaTime) {
        if (UITextureComponent.mapper.has(e)) {
            UITextureComponent texture = UITextureComponent.mapper.get(e);

            batch.draw(texture.texture, texture.x, texture.y);
        }

        if (UITextComponent.mapper.has(e)) {
            UITextComponent text = UITextComponent.mapper.get(e);

            if (flashTime >= flashInterval) {
                flashOn = !flashOn;
            }

            if (text.effect != UITextComponent.Effect.Flashing || flashOn) {
                font.draw(batch, text.x, text.y, text.text, text.align);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        flashTime += deltaTime;
        typeTime += deltaTime;

        batch.setProjectionMatrix(camera.combined);
        batch.setShader(shader);
        batch.begin();
        super.update(deltaTime);
        batch.end();

        if (flashTime >= flashInterval) {
            flashTime = 0.0f;
        }

        if (typeTime >= typeInterval) {
            typeTime = 0.0f;
        }
    }
}
