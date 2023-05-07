package uk.co.zacgarby.infiltrate.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import uk.co.zacgarby.infiltrate.components.ui.GPSComponent;
import uk.co.zacgarby.infiltrate.components.mechanics.PlayerComponent;
import uk.co.zacgarby.infiltrate.components.physical.PositionComponent;
import uk.co.zacgarby.infiltrate.components.ui.UITextComponent;

public class GPSSystem extends IntervalSystem {
    private PositionComponent playerPosition;
    private UITextComponent hud;
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
            this.hud = UITextComponent.mapper.get(text);
        }

        String location = getLocation(playerPosition.position);
        if (location != null) {
            hud.text = "* " + location;
        } else {
            // no matches
            hud.text = "* -";
        }
    }

    public String getLocation(Vector2 position) {
        MapLayer layer = map.getLayers().get("Areas");
        MapObjects objects = layer.getObjects();

        for (MapObject object : objects) {
            RectangleMapObject rect = (RectangleMapObject) object;
            if (rect.getRectangle().contains(position)) {
                return object.getName();
            }
        }

        return null;
    }
}
