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
import com.brinkman.platformer.util.AssetUtil;

import static com.brinkman.platformer.util.AssetUtil.ASSET_MANAGER;
import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class TMXMap {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    public static float mapWidth;
    public static float mapHeight;

    /**
     * Constructs the TMXMap Object.
     * @param batch SpriteBatch
     * @param tmxFilePath String url
     */
    public TMXMap(SpriteBatch batch, String tmxFilePath) {
        map = (TiledMap) AssetUtil.getAsset(tmxFilePath, TiledMap.class);
        renderer = new OrthogonalTiledMapRenderer(map, TO_WORLD_UNITS, batch);
        MapProperties mapProperties = map.getProperties();

        int width = mapProperties.get("width", Integer.class);
        int height = mapProperties.get("height", Integer.class);
        int tileWidth = mapProperties.get("tilewidth", Integer.class);
        int tileHeight = mapProperties.get("tileheight", Integer.class);

        mapWidth = width * tileWidth * TO_WORLD_UNITS;
        mapHeight = height * tileHeight * TO_WORLD_UNITS;
    }

    /**
     * Uses the given String url to find and return MapObjects.
     * @param layerName String url
     *
     * @return MapObjects
     */
    public MapObjects getMapObjects(String layerName) { return map.getLayers().get(layerName).getObjects(); }

    /**
     * Returns the collision rectangles for MapObject objects in the "collision" layer.
     * @return Array<Rectangle>
     */
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

    public Rectangle getMapObjectBounds(MapObject object) {
        float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
        float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
        float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
        float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

        return new Rectangle(x, y, width, height);
    }

    public Array<Rectangle> getSawCollisionRectangles() {
        Array<Rectangle> rectangles = new Array<>();
        for (MapObject object : getMapObjects("saw")) {
            float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
            float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
            float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

            Rectangle bounds = new Rectangle(x, y, width, height);

            rectangles.add(bounds);
        }
        return rectangles;
    }

    /**
     * Renders all layers of map.
     * @param cam OrthographicCamera
     */
    public void render(OrthographicCamera cam) {
        renderer.setView(cam);
        renderer.render();
    }

    /**
     * Renders selected layers, in selected order, of map.
     * @param cam OrthographicCamera
     * @param layers int[]
     */
    public void render(OrthographicCamera cam, int[] layers) {
        renderer.setView(cam);
        renderer.render(layers);
    }

    /**
     * Disposes of the TiledMap object.
     */
    public void dispose() { map.dispose(); }
}
