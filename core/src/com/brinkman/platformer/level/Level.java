package com.brinkman.platformer.level;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brinkman.platformer.map.TMXMap;


/**
 * Created by Austin on 10/21/2016.
 */
public class Level {

    private final TMXMap map;

    private int levelNumber;
    private boolean hasKey;

    /**
     * Constructs the Level Object.  The Level object holds the TMXMap data.
     * @param levelNumber int
     * @param batch SpriteBatch
     */
    public Level(int levelNumber, SpriteBatch batch) {
        this.levelNumber = levelNumber;

        map = new TMXMap(batch, "map/World 1/level" + levelNumber + ".tmx");
        hasKey = map.getMapObjects("key").getCount() > 0;
    }

    public Level(String levelPath, SpriteBatch batch) {
        map = new TMXMap(batch, levelPath);
    }

    /**
     * Returns the value of int levelNumber.
     * @return int
     */
    public int getLevelNumber() { return levelNumber; }

    /**
     * Returns boolean value representing whether or not the Level has a key.
     * @return boolean
     */
    public boolean hasKey() { return hasKey; }

    /**
     * Returns the TMXMap map.
     * @return TMXMap
     */
    public TMXMap getTmxMap() { return map; }

    /**
     * Handles the renderering of the TMXMap.
     * @param camera OrthographicCamera
     */
    public void render(OrthographicCamera camera) {
        map.render(camera);
    }

    /**
     * Disposes of the TMXMap.
     */
    public void dispose() {
        map.dispose();
    }

}
