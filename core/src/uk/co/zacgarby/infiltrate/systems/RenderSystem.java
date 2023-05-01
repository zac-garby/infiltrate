package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.co.zacgarby.infiltrate.components.Families;
import uk.co.zacgarby.infiltrate.components.PositionComponent;
import uk.co.zacgarby.infiltrate.components.TextureComponent;
import uk.co.zacgarby.infiltrate.components.TextureSliceComponent;

public class RenderSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;
    private ShaderProgram nullShader;
    private final FrameBuffer destFBO, wallsFBO;
    private final TextureRegion destFBORegion, wallsFBORegion;
    private final Matrix4 idMatrix;
    private final Texture mapMask;

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera, ShaderProgram shader, Texture mapMask) {
        super(Families.renderable);
        this.batch = batch;
        this.camera = camera;
        this.priority = 1000;
        this.shader = shader;
        this.mapMask = mapMask;

        destFBO = new FrameBuffer(Pixmap.Format.RGB888, (int) camera.viewportWidth, (int) camera.viewportHeight, false);
        Texture destFBOTex = destFBO.getColorBufferTexture();
        destFBOTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        destFBORegion = new TextureRegion(destFBOTex);
        destFBORegion.flip(false, true);

        wallsFBO = new FrameBuffer(Pixmap.Format.RGB888, (int) camera.viewportWidth, (int) camera.viewportHeight, false);
        Texture wallsFBOTex = wallsFBO.getColorBufferTexture();
        wallsFBOTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        wallsFBORegion = new TextureRegion(wallsFBOTex);
        wallsFBORegion.flip(false, true);

        OrthographicCamera idCamera = new OrthographicCamera(camera.viewportWidth, camera.viewportHeight);
        idCamera.translate(camera.viewportWidth / 2, camera.viewportHeight / 2);
        idCamera.update();
        idMatrix = idCamera.combined;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        nullShader = batch.getShader();
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);

        batch.setShader(nullShader);
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

        ScreenUtils.clear(1, 0, 0, 1);

        // render game to lower-res FBO
        batch.setProjectionMatrix(camera.combined);

        // enable the lighting shader
        batch.setShader(shader);

        destFBO.begin();
        batch.begin();

        // give the shader the map mask, for lighting purposes
        mapMask.bind(1);
        shader.setUniformi("u_mask", 1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        shader.setUniformf("u_cam_x", camera.position.x / mapMask.getWidth());
        shader.setUniformf("u_cam_y", camera.position.y / mapMask.getHeight());
        shader.setUniformf("u_width", mapMask.getWidth());
        shader.setUniformf("u_height", mapMask.getHeight());

        // render the renderables
        super.update(dt);

        // end FBO batch
        batch.end();
        destFBO.end();


        // render FBO to the actual screen
        batch.setProjectionMatrix(idMatrix);
        batch.setShader(nullShader);
        batch.begin();
        batch.draw(destFBORegion, 0, 0, camera.viewportWidth, camera.viewportHeight);
        batch.end();
    }
}
