package com.brinkman.platformer.terrain;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class TMXMap {
    private TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    public static int width;
    public static int height;
    public static float mapWidth;
    public static float mapHeight;
    public static int tileWidth;
    public static int tileHeight;

    public TMXMap(SpriteBatch batch, String tmxFilePath) {
        map = new TmxMapLoader().load(tmxFilePath);
        renderer = new OrthogonalTiledMapRenderer(map, TO_WORLD_UNITS, batch);
        MapProperties mapProperties = map.getProperties();

        width = mapProperties.get("width", Integer.class);
        height = mapProperties.get("height", Integer.class);
        tileWidth = mapProperties.get("tilewidth", Integer.class);
        tileHeight = mapProperties.get("tileheight", Integer.class);

        mapWidth = width * tileWidth * TO_WORLD_UNITS;
        mapHeight = height * tileHeight * TO_WORLD_UNITS;
    }

      public TiledMap getMap() { return map; }

    public MapObjects getMapObjects(String layerName) { return map.getLayers().get(layerName).getObjects(); }

    public Array<Rectangle> getMapCollisionRectangles() {
        Array<Rectangle> rectangles = new Array<Rectangle>();
        for (MapObject object : getMapObjects("collision")) {
            float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
            float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
            float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

            Rectangle bounds = new Rectangle(x, y, width, height);

            rectangles.add(bounds);
        }

        return rectangles;
    }

    public OrthogonalTiledMapRenderer getRenderer() { return renderer; }

    public void render(OrthographicCamera cam) {
        renderer.setView(cam);
        renderer.render();
    }

    public void render(OrthographicCamera cam, int[] layers) {
        renderer.setView(cam);
        renderer.render(layers);
    }

    public void dispose() { map.dispose(); }
}
