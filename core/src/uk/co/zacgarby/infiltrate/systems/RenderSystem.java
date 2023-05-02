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
import uk.co.zacgarby.infiltrate.Font;
import uk.co.zacgarby.infiltrate.components.graphics.HiddenComponent;
import uk.co.zacgarby.infiltrate.components.graphics.TextComponent;
import uk.co.zacgarby.infiltrate.components.graphics.TextureComponent;
import uk.co.zacgarby.infiltrate.components.graphics.TextureSliceComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.MovementControlsComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.PlayerComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;

import java.util.Arrays;
import java.util.Comparator;

public class RenderSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final ShaderProgram shader;
    private ShaderProgram nullShader;
    private final FrameBuffer destFBO, wallsFBO;
    private final TextureRegion destFBORegion, wallsFBORegion;
    private final Matrix4 idMatrix;
    private final Texture mapMask;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Font font;
    private ImmutableArray<Entity> uiEntities;

    private final Comparator<Object> yComparator = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            PositionComponent s1 = PositionComponent.mapper.get((Entity) o1);
            PositionComponent s2 = PositionComponent.mapper.get((Entity) o2);
            return (int) Math.signum(s2.position.y - s1.position.y);
        }
    };

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera, ShaderProgram shader, TiledMap map, Texture mapMask) {
        super(Family
                .all(TextureComponent.class, PositionComponent.class)
                .exclude(HiddenComponent.class).get());

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

        mapRenderer = new OrthogonalTiledMapRenderer(map, batch);

        font = new Font(
                Gdx.files.internal("img/font.png"),
                "abcdefghijklmnopqrstuvwxyz0123456789.,()[]{}<>/\\|!-_+=;:?'\"%#~*"
        );
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        uiEntities = engine.getEntitiesFor(Family.one(TextComponent.class).get());

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

        ScreenUtils.clear(0, 0, 0, 1);

        // render game to lower-res FBO
        batch.setProjectionMatrix(camera.combined);

        // enable the lighting shader
        batch.setShader(shader);

        destFBO.begin();

        mapRenderer.setView(camera);
        mapRenderer.setMap(mapRenderer.getMap());
        mapRenderer.render();

        batch.begin();

        // give the shader the map mask, for lighting purposes
        mapMask.bind(1);
        shader.setUniformi("u_mask", 1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        shader.setUniformf("u_cam_x", camera.position.x / mapMask.getWidth());
        shader.setUniformf("u_cam_y", camera.position.y / mapMask.getHeight());
        shader.setUniformf("u_width", mapMask.getWidth());
        shader.setUniformf("u_height", mapMask.getHeight());

        // heading vector for player heading
        Entity player = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        MovementControlsComponent control = MovementControlsComponent.mapper.get(player);
        shader.setUniformf("u_heading", control.heading);

        // render the renderables, in y-order
        Object[] entities = this.getEntities().toArray();

        Arrays.sort(entities, yComparator);

        for (Object e : entities) {
            this.processEntity((Entity) e, dt);
        }

        // end FBO batch
        batch.end();
        destFBO.end();


        // render FBO to the actual screen
        batch.setProjectionMatrix(idMatrix);
        batch.setShader(nullShader);
        batch.begin();
        batch.draw(destFBORegion, 0, 0, camera.viewportWidth, camera.viewportHeight);

        // draw ui
        for (Entity e : uiEntities) {
            if (TextComponent.mapper.has(e)) {
                TextComponent text = TextComponent.mapper.get(e);
                if (text.alignLeft) {
                    font.draw(batch, text.x, text.y, text.text);
                } else {
                    font.drawRight(batch, text.x, text.y, text.text);
                }
            }
        }

        batch.end();
    }
}
