package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import uk.co.zacgarby.infiltrate.etc.Font;
import uk.co.zacgarby.infiltrate.components.graphics.TextComponent;

public class UIRenderSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final Font font;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;

    public UIRenderSystem(SpriteBatch batch, float viewportWidth, float viewportHeight) {
        super(Family.one(TextComponent.class).get(), 1100);
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
        if (TextComponent.mapper.has(e)) {
            TextComponent text = TextComponent.mapper.get(e);
            if (text.alignLeft) {
                font.draw(batch, text.x, text.y, text.text);
            } else {
                font.drawRight(batch, text.x, text.y, text.text);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.setShader(shader);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }
}
