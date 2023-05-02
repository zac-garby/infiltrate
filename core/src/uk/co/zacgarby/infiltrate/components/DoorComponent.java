package uk.co.zacgarby.infiltrate.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class DoorComponent implements Component, InteractionComponent.Interaction {
    public final int tileX, tileY;

    // -1 for no lock
    // any other ID corresponds to a key
    public final int lockID = -1;
    public final boolean horizontal;

    public DoorComponent(int tileX, int tileY, boolean horizontal) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.horizontal = horizontal;
    }

    @Override
    public void doInteraction(Engine engine, Entity entity) {
        Entity mapEntity = engine.getEntitiesFor(Family.all(MapComponent.class).get()).first();
        MapComponent map = MapComponent.mapper.get(mapEntity);

        TiledMapTileLayer layer = (TiledMapTileLayer) map.map.getLayers().get(0);
        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
        int id = cell.getTile().getId();

        boolean open = id == 1 || id == 4;

        // TODO: make work

        if (open) {
            cell.setTile(map.map.getTileSets().getTile(id + 2));
        } else {
            cell.setTile(map.map.getTileSets().getTile(id - 2));
        }
//
//        for (int i = 0; i < layer.getWidth(); i++) {
//            for (int j = 0; j < layer.getHeight(); j++) {
//                layer.getCell(i, j).setTile(
//                        map.map.getTileSets().getTile(0)
//                );
//            }
//        }
    }
}
