package com.brinkman.platformer.level;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brinkman.platformer.terrain.TMXMap;


/**
 * Created by Austin on 10/21/2016.
 */
public class Level {

    private final TMXMap map;

    private final int levelNumber;

    /**
     * Constructs the Level Object.  The Level object holds the TMXMap data.
     * @param levelNumber int
     * @param batch SpriteBatch
     */
    public Level(int levelNumber, SpriteBatch batch) {
        this.levelNumber = levelNumber;
        map = new TMXMap(batch, "terrain/level" + levelNumber + ".tmx");
    }

    /**
     * Returns the value of int levelNumber.
     * @return int
     */
    public int getLevelNumber() { return levelNumber; }

    /**
     * Returns the TMXMap map.
     * @return TMXMap
     */
    public TMXMap getMap() { return map; }

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
