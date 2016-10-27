package com.brinkman.platformer.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.brinkman.platformer.entity.Actor;
import com.brinkman.platformer.entity.Coin;
import com.brinkman.platformer.entity.Player;
import com.brinkman.platformer.terrain.TMXMap;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 10/21/2016.
 */
public class Level {
    private TMXMap map;

    private int levelNumber;

    public Level(int levelNumber, SpriteBatch batch) {
        this.levelNumber = levelNumber;
        map = new TMXMap(batch, "terrain/level" + levelNumber + ".tmx");
    }

    public int getLevelNumber() { return levelNumber; }

    public TMXMap getMap() { return map; }

    public void render(OrthographicCamera camera) {
        map.render(camera);
    }

    public void dispose() {
        map.dispose();
    }

}
