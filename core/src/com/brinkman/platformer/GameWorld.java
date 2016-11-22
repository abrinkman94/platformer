package com.brinkman.platformer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.entity.*;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.util.TexturePaths;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 11/5/2016.
 */
public class GameWorld {
    private Level level;
    private Map<Entity, String> entities;

    private static Logger LOGGER = new Logger(GameWorld.class.getName(), Logger.DEBUG);

    public GameWorld(Level level) {
        this.level = level;
        entities = new HashMap<>(128);

        LOGGER.info("Initialized");
    }

    public Level getLevel() { return level; }

    public void setLevel(Level level) { this.level = level; }

    public Map<Entity, String> getEntities() { return entities; }

    public Entity getEntityByValue(String value) {
        for (Map.Entry<Entity, String> entry : entities.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

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
        }
        else {
            LOGGER.info("Entity type error");
        }
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void initializeMapObjects(SpriteBatch batch, Array<Coin> coins, Array<Saw> saws) {
        for (MapObject object : level.getMap().getMapObjects("coins")) {
            float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;

            final float coinYOffset = 1.0f;
            Coin coin = new Coin(batch, x, y + coinYOffset);
            coins.add(coin);
            addEntity(coin);
        }

        if (level.getMap().getMapObjects("saw") != null) {
            for (MapObject sawObject : level.getMap().getMapObjects("saw")) {
                float x = sawObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = sawObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Saw saw = new Saw(batch, x, y);
                saws.add(saw);
                addEntity(saw);
            }
        }

        if (level.getMap().getMapObjects("life item") != null) {
            for (MapObject itemObject : level.getMap().getMapObjects("life item")) {
                float x = itemObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = itemObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Item item = new Item(TexturePaths.LIFE_ITEM_TEXTURE, ItemType.LIFE, x, y + 1);
                addEntity(item);
            }
        }

        if (level.getMap().getMapObjects("key") != null) {
            for (MapObject keyObject : level.getMap().getMapObjects("key")) {
                float x = keyObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = keyObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Item key = new Item(TexturePaths.KEY_TEXTURE, ItemType.KEY, x, y + 1);
                addEntity(key);
            }
        }
    }

    public void render(OrthographicCamera camera, float delta, Batch batch) {
        level.getMap().render(camera);

        for (Entity entity : entities.keySet()) {
            entity.render(delta, batch);
        }
    }

    public void dispose() {
        level.dispose();
        entities.keySet().forEach(Entity::dispose);

        LOGGER.info("Disposed");
    }
}
