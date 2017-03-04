package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.component.*;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.physics.StaticPhysicsComponent;
import com.brinkman.platformer.component.render.AnimationRenderComponent;
import com.brinkman.platformer.component.render.RenderComponent;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.TexturePaths;
import com.google.common.collect.ImmutableClassToInstanceMap;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 10/1/2016.
 */
public class Coin extends Actor {
    private static final Logger LOGGER = new Logger("Coin", Logger.DEBUG);

    private final Animation animations;

    private static final float ANIMATION_TIME = 0.025f;
    private static final int COIN_SIZE = 64;

    private final ImmutableClassToInstanceMap<RootComponent> components;

    /**
     * Constructs the Coin object.
     * @param x float x position
     * @param y float y position
     */
    public Coin(float x, float y) {
        elapsedTime = 0;

        StaticPhysicsComponent body = new StaticPhysicsComponent();
        body.getPosition().set(x, y);
        body.setWidth(COIN_SIZE * TO_WORLD_UNITS);
        body.setHeight(COIN_SIZE  * TO_WORLD_UNITS);
        body.setCollisionListener(Player.class, this::handlePlayerCollision);

        texture = (Texture) AssetUtil.getAsset(TexturePaths.COIN_SPRITESHEET, Texture.class);

        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / 8, texture.getHeight() / 8);

        TextureRegion[] textureRegions = new TextureRegion[8 * 8];
        int index = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                textureRegions[index++] = tmp[i][j];
            }
        }

        animations = new Animation<>(ANIMATION_TIME, textureRegions);

        components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .put(RenderComponent.class, new AnimationRenderComponent(animations))
                .put(PhysicsComponent.class, body)
                .build();
    }

    private void handlePlayerCollision(Player player) {
        animations.setFrameDuration(0.002f);

        Body body = components.getInstance(PhysicsComponent.class);
        assert body != null;
        if (body.getWidth() > 0.5f) {
            animateCollect(-0.25f);
        } else {
            body.setRemovedOnCollision(true);
        }
    }

    private void animateCollect(float increment) {
        Body body = components.getInstance(PhysicsComponent.class);
        assert body != null;
        float width = body.getWidth();
        float height = body.getHeight();
        body.setWidth(width + increment);
        body.setHeight(height + increment);
    }

    @Override
    public void dispose() { texture.dispose(); }

    @Override
    public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }
}
