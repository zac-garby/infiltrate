package uk.co.zacgarby.infiltrate;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public abstract class Map {
    public static Texture makeMapMask(TiledMap map, Pixmap maskTileset) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);

        Pixmap pm = new Pixmap(
                layer.getWidth() * layer.getTileWidth(),
                layer.getHeight() * layer.getTileHeight(),
                Pixmap.Format.RGBA8888
        );

        pm.setColor(0, 0, 0, 0);
        pm.fill();
        pm.setColor(1, 1, 1, 1);

        for (int y = 0; y < layer.getHeight(); y++) {
            for (int x = 0; x < layer.getWidth(); x++) {
                TiledMapTile cell = layer.getCell(x, y).getTile();
                TextureRegion region = cell.getTextureRegion();

                pm.drawPixmap(
                        maskTileset,
                        region.getRegionX(), region.getRegionY(),
                        region.getRegionWidth(), region.getRegionHeight(),
                        x * region.getRegionWidth(),
                        (layer.getHeight() - y - 1) * region.getRegionHeight(),
                        region.getRegionWidth(), region.getRegionHeight()
                );
            }
        }

        return new Texture(pm, Pixmap.Format.RGBA8888, false);
    }
}
