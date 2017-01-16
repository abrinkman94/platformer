package com.brinkman.platformer.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.util.AssetUtil;

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
