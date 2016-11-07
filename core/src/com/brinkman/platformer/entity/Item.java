package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.util.Constants;

/**
 * Created by Austin on 11/7/2016.
 */

public class Item extends Actor {
    private static final Logger LOGGER = new Logger(Item.class.getName());

    private ItemType itemType;

    public Item(String texturePath, ItemType itemType, float x, float y) {
        this.itemType = itemType;

        height = 40 * Constants.TO_WORLD_UNITS;
        width = 39 * Constants.TO_WORLD_UNITS;
        position = new Vector2(x, y);

        texture = new Texture(texturePath);

        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(position.x, position.y);
    }

    public ItemType getItemType() { return itemType; }

    @Override
    public void handleDeath() {

    }

    @Override
    public void render(float dt, Batch batch) {
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        LOGGER.info("Disposed");
    }
}
