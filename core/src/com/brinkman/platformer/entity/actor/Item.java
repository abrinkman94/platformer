package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.physics.Collidable;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.Constants;

import java.util.Iterator;
import java.util.Map;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 11/7/2016.
 */

public class Item extends Actor {
    private static final Logger LOGGER = new Logger(Item.class.getName(), Logger.DEBUG);

    private ItemType itemType;

    public Item(String texturePath, ItemType itemType, float x, float y) {
        this.itemType = itemType;

        height = 40 * TO_WORLD_UNITS;
        width = 39 * TO_WORLD_UNITS;
        position = new Vector2(x, y);

        texture = (Texture) AssetUtil.getAsset(texturePath, Texture.class);

        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(position.x, position.y);
    }

    @Override
    public Shape2D getBounds() {
        return new Rectangle(position.x, position.y, width * TO_WORLD_UNITS, height * TO_WORLD_UNITS);
    }

    @Override
    public void handleCollisionEvent(GameWorld world) {
        Player player = (Player) world.getEntityByValue("player");
        Rectangle playerBounds = (Rectangle) player.getBounds();

        if (Intersector.overlaps((Rectangle) getBounds(), playerBounds)) {
            world.removeEntity(this);

            if (itemType == ItemType.LIFE) {
                player.setLives(player.getLives() + 1);
            } else if (itemType == ItemType.KEY) {
                player.getItems().put(this, ItemType.KEY);
            }
        }
    }

    @Override
    public void render(float dt, Batch batch) {
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();

        LOGGER.info("Disposed");
    }
}

