package uk.co.zacgarby.infiltrate.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class PhysicsWorldComponent implements Component {
    public static final ComponentMapper<PhysicsWorldComponent> mapper = ComponentMapper.getFor(PhysicsWorldComponent.class);

    public final World world;
    public final ArrayList<Body> tileBodies = new ArrayList<>();
    public float tileSize;

    public PhysicsWorldComponent(TiledMap map) {
        world = new World(new Vector2(0, 0), true);

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);

        tileSize = layer.getTileWidth();

        for (int y = 0; y < layer.getHeight(); y++) {
            for (int x = 0; x < layer.getWidth(); x++) {
                if ((boolean) layer.getCell(x, y).getTile().getProperties().get("collides")) {
                    BodyDef bodyDef = new BodyDef();
                    bodyDef.position.set(x * tileSize + tileSize / 2, y * tileSize + tileSize / 2);

                    Body body = world.createBody(bodyDef);

                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(tileSize / 2, tileSize / 2);
                    body.createFixture(shape, 0f);
                    shape.dispose();

                    tileBodies.add(body);
                }
            }
        }
    }
}
