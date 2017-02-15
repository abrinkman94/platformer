package com.brinkman.platformer.entity.actor.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.component.*;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.util.AssetUtil;
import com.google.common.collect.ImmutableClassToInstanceMap;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 11/7/2016.
 */

public class Item extends Actor {
    private static final Logger LOGGER = new Logger(Item.class.getName(), Logger.DEBUG);

    private final ItemType itemType;

    private final ImmutableClassToInstanceMap<RootComponent> components;

    /**
     * Constructs new Item with given parameters.
     * @param texturePath String
     * @param itemType ItemType
     * @param x float
     * @param y float
     */
    public Item(String texturePath, ItemType itemType, float x, float y) {
        this.itemType = itemType;

        ControlledPhysicsComponent body = new ControlledPhysicsComponent();
        body.setRemovedOnCollision(true);
        body.setHeight(32 * TO_WORLD_UNITS);
        body.setWidth(32 * TO_WORLD_UNITS);
        body.getPosition().set(x, y);

        texture = (Texture) AssetUtil.getAsset(texturePath, Texture.class);

        float width = body.getWidth();
        float height = body.getHeight();
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        Vector2 position = body.getPosition();
        sprite.setPosition(position.x, position.y);

        components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .put(RenderComponent.class, new SpriteRenderComponent(sprite))
                .put(ControlledPhysicsComponent.class, body)
                .build();
    }

    private void render(float dt, Batch batch) {
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

    @Override
    public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }
}

