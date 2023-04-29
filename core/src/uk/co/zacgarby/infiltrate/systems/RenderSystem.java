package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.co.zacgarby.infiltrate.components.Families;
import uk.co.zacgarby.infiltrate.components.PositionComponent;
import uk.co.zacgarby.infiltrate.components.TextureComponent;
import uk.co.zacgarby.infiltrate.components.TextureSliceComponent;

public class RenderSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final Camera camera;

    public RenderSystem(SpriteBatch batch, Camera camera) {
        super(Families.renderable);
        this.batch = batch;
        this.camera = camera;
        this.priority = 1000;
    }

    @Override
    protected void processEntity(Entity entity, float dt) {
        TextureComponent texture = TextureComponent.mapper.get(entity);
        PositionComponent position = PositionComponent.mapper.get(entity);

        if (TextureSliceComponent.mapper.has(entity)) {
            TextureSliceComponent slice = TextureSliceComponent.mapper.get(entity);

            batch.draw(
                    texture.texture,
                    Math.round(position.position.x - texture.originX),
                    Math.round(position.position.y - texture.originY),
                    texture.originX, texture.originY,
                    texture.width, texture.height,
                    1f, 1f, 0f,
                    slice.sliceX * slice.sliceWidth, slice.sliceY * slice.sliceHeight,
                    slice.sliceWidth, slice.sliceHeight,
                    false, false);
        } else {
            batch.draw(
                    texture.texture,
                    Math.round(position.position.x - texture.originX),
                    Math.round(position.position.y - texture.originY),
                    texture.originX, texture.originY,
                    texture.width, texture.height,
                    1f, 1f, 0f,
                    0, 0,
                    texture.texture.getWidth(), texture.texture.getHeight(),
                    false, false);
        }
    }

    @Override
    public void update(float dt) {
        camera.update();

        ScreenUtils.clear(0, 0, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(dt);
        batch.end();
    }
}
