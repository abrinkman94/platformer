package com.brinkman.platformer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.entity.actor.*;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.util.TexturePaths;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 11/5/2016.
 */
public class GameWorld {
    private Level level;
    private final Map<Entity, String> entities;

    private final int[] backgroundLayers = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private final int[] foregroundLayers = {9};

    private static final Logger LOGGER = new Logger(GameWorld.class.getName(), Logger.DEBUG);

    /**
     * Generates GameWorld containing the Level and all objects within.
     * @param level Level
     */
    public GameWorld(Level level) {
        this.level = level;
        entities = new ConcurrentHashMap<>(128);

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
    public Map<Entity, String> getEntities() { return entities; }

    /**
     * Returns Entity with given value.
     * @param value String
     *
     * @return Entity
     */
    public Entity getEntityByValue(String value) {
        for (Entry<Entity, String> entry : entities.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Adds Entity to Map.
     * @param entity Entity
     */
    public void addEntity(Entity entity) {
        if (entity instanceof Player) {
            entities.put(entity, "player");
        } else if (entity instanceof Enemy) {
            entities.put(entity, "enemy");
        } else if (entity instanceof Saw) {
            entities.put(entity, "saw");
        } else if (entity instanceof Coin) {
            entities.put(entity, "coin");
        } else if (entity instanceof Item) {
            entities.put(entity, "item");
        } else if (entity instanceof StaticEntity) {
            entities.put(entity, "static entity");
        } else if (entity instanceof Exit) {
            entities.put(entity, "exit");
        }
        else {
            LOGGER.info("Entity type error");
        }
    }

    /**
     * Removes Entity from Map.
     * @param entity Entity
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public int getNumberOfCoins() {
        int numberOfCoins = 0;

        for (Entity entity : entities.keySet()) {
            if (entity instanceof Coin) {
                numberOfCoins++;
            }
        }

        return numberOfCoins;
    }

    /**
     * Initializes all dynamic objects in Level.
     */
    public void initializeMapObjects() {
        for (MapObject object : level.getMap().getMapObjects("collision")) {
            float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
            float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
            float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

            Entity staticEntity = new StaticEntity(x, y, width, height);
            addEntity(staticEntity);
        }

        for (MapObject object : level.getMap().getMapObjects("coins")) {
            float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;

            final float coinYOffset = 1.0f;
            Entity coin = new Coin(x, y + coinYOffset);
            addEntity(coin);
        }

        if (level.getMap().getMapObjects("saw") != null) {
            for (MapObject sawObject : level.getMap().getMapObjects("saw")) {
                float x = sawObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = sawObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Entity saw = new Saw(x, y);
                addEntity(saw);
            }
        }

        if (level.getMap().getMapObjects("life item") != null) {
            for (MapObject itemObject : level.getMap().getMapObjects("life item")) {
                float x = itemObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = itemObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Entity item = new Item(TexturePaths.LIFE_ITEM_TEXTURE, ItemType.LIFE, x, y + 1);
                addEntity(item);
            }
        }

        if (level.getMap().getMapObjects("key") != null) {
            for (MapObject keyObject : level.getMap().getMapObjects("key")) {
                float x = keyObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = keyObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Entity key = new Item(TexturePaths.KEY_TEXTURE, ItemType.KEY, x, y + 1);
                addEntity(key);
            }
        }

        if (level.getMap().getMapObjects("exit") != null) {
            for (MapObject exitObject : level.getMap().getMapObjects("exit")) {
                float x = exitObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = exitObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;
                float width = exitObject.getProperties().get("width", float.class) * TO_WORLD_UNITS;
                float height = exitObject.getProperties().get("height", float.class) * TO_WORLD_UNITS;

                Entity exit = new Exit(this, x, y, width, height);
                addEntity(exit);
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
        level.getMap().render(camera, backgroundLayers);

        for (Entity entity : entities.keySet()) {
            entity.render(delta, batch);
        }

        level.getMap().render(camera, foregroundLayers);
    }

    /**
     * Disposes of disposable data in GameWorld.
     */
    public void dispose() {
        level.dispose();
        entities.keySet().forEach(Entity::dispose);

        LOGGER.info("Disposed");
    }
}
