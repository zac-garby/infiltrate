package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import uk.co.zacgarby.infiltrate.components.GPSComponent;
import uk.co.zacgarby.infiltrate.components.PlayerComponent;
import uk.co.zacgarby.infiltrate.components.PositionComponent;
import uk.co.zacgarby.infiltrate.components.TextComponent;

public class GPSSystem extends IntervalSystem {
    private PositionComponent playerPosition;
    private TextComponent hud;
    public final TiledMap map;

    public GPSSystem(TiledMap map) {
        super(0.1f, 500);
        this.map = map;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    @Override
    protected void updateInterval() {
        if (this.playerPosition == null) {
            Entity player = getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
            this.playerPosition = PositionComponent.mapper.get(player);

            Entity text = getEngine().getEntitiesFor(Family.all(GPSComponent.class).get()).first();
            this.hud = TextComponent.mapper.get(text);
        }

        MapLayer layer = map.getLayers().get(1);
        MapObjects objects = layer.getObjects();

        for (MapObject object : objects) {
            RectangleMapObject rect = (RectangleMapObject) object;
            if (rect.getRectangle().contains(playerPosition.position)) {
                hud.text = "* " + object.getName();
                return;
            }
        }

        // no matches
        hud.text = "* -";
    }
}
