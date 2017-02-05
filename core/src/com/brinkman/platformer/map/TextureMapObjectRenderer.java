package com.brinkman.platformer.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by Austin on 2/5/2017.
 */
public class TextureMapObjectRenderer extends OrthogonalTiledMapRenderer {

    public TextureMapObjectRenderer(TiledMap map) {
        super(map);
    }

    public TextureMapObjectRenderer(TiledMap map, Batch batch) {
        super(map, batch);
    }

    public TextureMapObjectRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    public TextureMapObjectRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }

    public void renderObject(MapObject object, float x, float y, float width, float height) {
        if (object instanceof TextureMapObject) {
            TextureMapObject textureObject = (TextureMapObject) object;
            batch.begin();
            batch.draw(
                    textureObject.getTextureRegion(), x, y, width, height);
            batch.end();
        }
    }
}
