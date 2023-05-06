package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import uk.co.zacgarby.infiltrate.components.graphics.UITextureComponent;
import uk.co.zacgarby.infiltrate.etc.Font;
import uk.co.zacgarby.infiltrate.components.graphics.UITextComponent;

public class UIRenderSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final Font font;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;
    private float time = 0.0f;
    private final float flashTime = 0.5f;
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

            if (time >= flashTime) {
                time = 0.0f;
                flashOn = !flashOn;
            }

            if (text.effect != UITextComponent.Effect.Flashing || flashOn) {
                font.draw(batch, text.x, text.y, text.text, text.align);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;

        batch.setProjectionMatrix(camera.combined);
        batch.setShader(shader);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }
}
