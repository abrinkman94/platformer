package com.brinkman.platformer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.entity.*;
import com.brinkman.platformer.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Austin on 11/5/2016.
 */
public class GameWorld {
    private Level level;
    private Map<Entity, String> entities;

    private static Logger LOGGER = new Logger("GameWorld");

    public GameWorld(Level level) {
        this.level = level;
        entities = new HashMap<>();
    }

    public Level getLevel() { return level; }

    public void setLevel(Level level) { this.level = level; }

    public Map<Entity, String> getEntities() { return entities; }

    public Entity getEntity(String value) {
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
        } else {
            LOGGER.info("Entity type error");
        }

    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void render(OrthographicCamera camera, float delta, Batch batch) {
        level.getMap().render(camera);

        for (Entity entity : entities.keySet()) {
            entity.render(delta, batch);
        }
    }
}
