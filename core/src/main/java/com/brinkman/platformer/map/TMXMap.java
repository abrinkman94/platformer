package com.brinkman.platformer.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.brinkman.platformer.util.AssetUtil;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class TMXMap extends TiledMap {
    private final TiledMap map;

    private OrthogonalTiledMapRenderer renderer;
    public static float mapWidth;
    public static float mapHeight;

    /**
     * Constructs the TMXMap Object.
     * @param tmxFilePath String url
     */
    public TMXMap(String tmxFilePath) {
        map = (TiledMap) AssetUtil.getAsset(tmxFilePath, TiledMap.class);

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
    public MapObjects getMapObjects(String layerName) {
        return (map.getLayers().get(layerName) != null) ? map.getLayers().get(layerName).getObjects() : null;
    }

    /**
     * Renders map.
     * @param cam OrthographicCamera
     * @param batch Batch
     */
    public void render(OrthographicCamera cam, Batch batch) {
        if(renderer == null) {
            renderer = new OrthogonalTiledMapRenderer(map, TO_WORLD_UNITS, batch);
        }
        renderer.setView(cam);
        renderer.render(new int[] {0});
    }

    public void renderNormal(OrthographicCamera cam, Batch batch) {
        if(renderer == null) {
            renderer = new OrthogonalTiledMapRenderer(map, TO_WORLD_UNITS, batch);
        }
        renderer.setView(cam);
        renderer.render(new int[] {1});
    }

    /**
     * Disposes of the TiledMap object.
     */
    public void dispose() { map.dispose(); }
}
