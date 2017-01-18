package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.physics.Collidable;
import com.brinkman.platformer.util.AssetUtil;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 11/7/2016.
 */

public class Item extends Actor {
    private static final Logger LOGGER = new Logger(Item.class.getName(), Logger.DEBUG);

    private final ItemType itemType;

    /**
     * Constructs new Item with given parameters.
     * @param texturePath String
     * @param itemType ItemType
     * @param x float
     * @param y float
     */
    public Item(String texturePath, ItemType itemType, float x, float y) {
        this.itemType = itemType;

        height = 32 * TO_WORLD_UNITS;
        getBody().setWidth(32 * TO_WORLD_UNITS);
        getBody().getPosition().set(x, y);

        texture = (Texture) AssetUtil.getAsset(texturePath, Texture.class);

        sprite = new Sprite(texture);
        sprite.setSize(getBody().getWidth(), height);
        Vector2 position = getBody().getPosition();
        sprite.setPosition(position.x, position.y);
    }

    @Override
    public Shape2D getBounds() {
        Vector2 position = getBody().getPosition();
        return new Rectangle(position.x, position.y, getBody().getWidth() * TO_WORLD_UNITS, height * TO_WORLD_UNITS);
    }

    @Override
    public void handleCollisionEvent(Collidable other) { }

    @Override
    public boolean shouldCollideWith(Collidable other) { return other instanceof Player; }

    @Override
    public boolean shouldBeRemovedOnCollision() { return true; }

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

    public ItemType getItemType() { return itemType; }
}

