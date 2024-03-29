package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.co.zacgarby.infiltrate.components.graphics.HiddenComponent;
import uk.co.zacgarby.infiltrate.components.graphics.TextureComponent;
import uk.co.zacgarby.infiltrate.components.graphics.TextureSliceComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementControlsComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.PlayerComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.TorchComponent;
import uk.co.zacgarby.infiltrate.components.physical.CameraFollowComponent;
import uk.co.zacgarby.infiltrate.components.physical.HeadingComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;

import java.util.Arrays;
import java.util.Comparator;

public class GameRenderSystem extends IteratingSystem {
    public boolean gameOver = false;

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;
    private final Texture mapMask;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private ImmutableArray<Entity> torches;
    private final float[] uCamX = new float[5], uCamY = new float[5];
    private final float[] uHeading = new float[10];
    private float time;

    private final Comparator<Object> yComparator = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            PositionComponent s1 = PositionComponent.mapper.get((Entity) o1);
            PositionComponent s2 = PositionComponent.mapper.get((Entity) o2);
            return (int) Math.signum(s2.position.y - s1.position.y);
        }
    };

    public GameRenderSystem(SpriteBatch batch, OrthographicCamera camera, ShaderProgram shader, TiledMap map, Texture mapMask) {
        super(Family
                .all(
                        TextureComponent.class,
                        PositionComponent.class
                ).exclude(HiddenComponent.class).get());

        this.batch = batch;
        this.camera = camera;
        this.priority = 1000;
        this.shader = shader;
        this.mapMask = mapMask;

        FrameBuffer wallsFBO = new FrameBuffer(Pixmap.Format.RGB888, (int) camera.viewportWidth, (int) camera.viewportHeight, false);
        Texture wallsFBOTex = wallsFBO.getColorBufferTexture();
        wallsFBOTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion wallsFBORegion = new TextureRegion(wallsFBOTex);
        wallsFBORegion.flip(false, true);

        mapRenderer = new OrthogonalTiledMapRenderer(map, batch);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        torches = engine.getEntitiesFor(Family.all(TorchComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
    }

    @Override
    protected void processEntity(Entity entity, float dt) {
        TextureComponent texture = TextureComponent.mapper.get(entity);
        PositionComponent position = PositionComponent.mapper.get(entity);

        if (TextureSliceComponent.mapper.has(entity)) {
            TextureSliceComponent slice = TextureSliceComponent.mapper.get(entity);

            float px = Math.round(position.position.x - texture.originX);
            float py = Math.round(position.position.y - texture.originY);


            if (CameraFollowComponent.mapper.has(entity)) {
                px = Math.round(camera.position.x - texture.originX);
                py = Math.round(camera.position.y - texture.originY);
            }

            batch.draw(
                    texture.texture,
                    px, py,
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
        time += dt;

        camera.update();

        // render game to lower-res FBO
        batch.setProjectionMatrix(camera.combined);

        // enable the lighting shader
        batch.setShader(shader);

        mapRenderer.setView(camera);
        mapRenderer.setMap(mapRenderer.getMap());
        mapRenderer.render();

        batch.begin();

        // give the shader the map mask, for lighting purposes
        mapMask.bind(1);
        shader.setUniformi("u_mask", 1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        for (int i = 0; i < torches.size(); i++) {
            Entity e = torches.get(i);
            PositionComponent position = PositionComponent.mapper.get(e);
            HeadingComponent heading = HeadingComponent.mapper.get(e);

            uCamX[i] = position.position.x / mapMask.getWidth();
            uCamY[i] = position.position.y / mapMask.getHeight();
            uHeading[2*i] = heading.heading.x;
            uHeading[2*i + 1] = heading.heading.y;
        }

        shader.setUniformi("u_num_players", torches.size());
        shader.setUniform1fv("u_cam_x", uCamX, 0, torches.size());
        shader.setUniform1fv("u_cam_y", uCamY, 0, torches.size());
        shader.setUniformf("u_width", mapMask.getWidth());
        shader.setUniformf("u_height", mapMask.getHeight());
        shader.setUniform2fv("u_heading", uHeading, 0, 2 * torches.size());
        shader.setUniformi("u_gameover", gameOver ? 1 : 0);
        shader.setUniformf("u_time", time);

        // render the renderables, in y-order
        Object[] entities = this.getEntities().toArray();

        Arrays.sort(entities, yComparator);

        for (Object e : entities) {
            this.processEntity((Entity) e, dt);
        }

        // end FBO batch
        batch.end();
    }
}
