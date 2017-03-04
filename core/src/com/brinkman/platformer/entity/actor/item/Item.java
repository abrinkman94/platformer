package com.brinkman.platformer.entity.actor.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.physics.StaticPhysicsComponent;
import com.brinkman.platformer.component.render.RenderComponent;
import com.brinkman.platformer.component.render.TextureRenderComponent;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.entity.actor.Player;
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

        StaticPhysicsComponent body = new StaticPhysicsComponent();
        body.setRemovedOnCollision(true);
        body.setHeight(32 * TO_WORLD_UNITS);
        body.setWidth(32 * TO_WORLD_UNITS);
        body.getPosition().set(x, y);
        body.setCollisionListener(Player.class, player -> body.setRemovedOnCollision(true));

        texture = (Texture) AssetUtil.getAsset(texturePath, Texture.class);

        TextureRegion sprite = new TextureRegion(texture);

        components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .put(RenderComponent.class, new TextureRenderComponent(sprite))
                .put(PhysicsComponent.class, body)
                .build();
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

