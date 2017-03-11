package com.brinkman.platformer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.entity.actor.*;
import com.brinkman.platformer.entity.actor.platform.Platform;
import com.brinkman.platformer.entity.actor.platform.PlatformType;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.map.TextureMapObjectRenderer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 11/5/2016.
 */
public class GameWorld {
    private Level level;
    private final List<Entity> entities;
    private final List<Entity> unmodifiableEntities;
    private TextureMapObjectRenderer textureMapObjectRenderer;

    private final int[] backgroundLayers = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private final int[] foregroundLayers = {9};

    private static final Logger LOGGER = new Logger(GameWorld.class.getName(), Logger.DEBUG);

    /**
     * Generates GameWorld containing the Level and all objects within.
     * @param level Level
     */
    public GameWorld(Level level) {
        this.level = level;
        entities = new LinkedList<>();
        unmodifiableEntities = Collections.unmodifiableList(entities);

        LOGGER.info("Initialized");
    }

    /**
     * Returns current Level of GameWorld.
     * @return Level
     */
    public Level getLevel() { return level; }

    /**
     * Sets Level of GameWorld.
     * @param level Level
     */
    public void setLevel(Level level) { this.level = level; }

    /**
     * Returns Map(Entity, String) of entities.
     * @return Map
     */
    public List<Entity> getEntities() { return unmodifiableEntities; }

    /**
     * Adds Entity to Map.
     * @param entity Entity
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Removes Entity from Map.
     * @param entity Entity
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    /**
     * Initializes all dynamic objects in Level.
     */
    public void initializeMapObjects() {
        if (level.getTmxMap().getMapObjects("Static Collision") != null) {
            for (MapObject object : level.getTmxMap().getMapObjects("Static Collision")) {
                float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
                float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
                float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

                Entity staticEntity = new StaticEntity(x, y, width, height);
                addEntity(staticEntity);
            }
        }

        if (level.getTmxMap().getMapObjects("collision") != null) {
            for (MapObject object : level.getTmxMap().getMapObjects("collision")) {
                float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
                float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
                float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

                Entity staticEntity = new StaticEntity(x, y, width, height);
                addEntity(staticEntity);
            }
        }

        if (level.getTmxMap().getMapObjects("saw") != null) {
            for (MapObject sawObject : level.getTmxMap().getMapObjects("saw")) {
                float x = sawObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = sawObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Entity saw = new Saw(x, y);
                addEntity(saw);
            }
        }

        if (level.getTmxMap().getMapObjects("falling platform") != null) {
            for (MapObject fallingPlatformObject : level.getTmxMap().getMapObjects("falling platform")) {
                float x = fallingPlatformObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = fallingPlatformObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;
                float width = fallingPlatformObject.getProperties().get("width", float.class) * TO_WORLD_UNITS;
                float height = fallingPlatformObject.getProperties().get("height", float.class) * TO_WORLD_UNITS;

                TextureRegion texture = ((TextureMapObject)fallingPlatformObject).getTextureRegion();
                Entity platform = new Platform(texture, x, y, width, height, PlatformType.FALLING);
                addEntity(platform);
            }
        }
    }

    /**
     * Renders the GameWorld.
     * @param camera OrthographicCamera
     * @param delta float
     * @param batch SpriteBatch
     */
    public void render(OrthographicCamera camera, float delta, Batch batch) {
        level.render(camera, batch);
        if (textureMapObjectRenderer == null) {
            textureMapObjectRenderer = new TextureMapObjectRenderer(level.getTmxMap(), batch);
        }
    }

    public void renderNormal(OrthographicCamera camera, float delta, Batch batch) {
        level.renderNormal(camera, batch);
        if (textureMapObjectRenderer == null) {
            textureMapObjectRenderer = new TextureMapObjectRenderer(level.getTmxMap(), batch);
        }
    }

    /**
     * Disposes of disposable data in GameWorld.
     */
    public void dispose() {
        level.dispose();
        entities.forEach(Entity::dispose);

        LOGGER.info("Disposed");
    }
}
