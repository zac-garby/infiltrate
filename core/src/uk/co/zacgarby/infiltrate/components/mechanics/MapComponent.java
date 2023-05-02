package uk.co.zacgarby.infiltrate.components.mechanics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class MapComponent implements Component {
    public static final ComponentMapper<MapComponent> mapper = ComponentMapper.getFor(MapComponent.class);
    public final TiledMap map;

    public MapComponent(TiledMap map) {
        this.map = map;
    }
}
