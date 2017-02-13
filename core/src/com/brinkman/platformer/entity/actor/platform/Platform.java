package com.brinkman.platformer.entity.actor.platform;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.brinkman.platformer.component.PhysicsComponent;
import com.brinkman.platformer.component.RenderComponent;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.util.Constants;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * Created by Austin on 2/3/2017.
 */
public class Platform  extends Actor{
    private final PlatformType platformType;

    private boolean touched;

    private final ImmutableClassToInstanceMap<RootComponent> components;

    public Platform(float x, float y, float width, float height, PlatformType platformType) {
        components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .put(RenderComponent.class, this::render)
                .put(PhysicsComponent.class, new PhysicsComponent())
                .build();

        Body body = components.getInstance(PhysicsComponent.class);
        assert body != null;

        body.getPosition().set(x, y);
        body.setWidth(width);
        body.setHeight(height);
        body.setCollisionListener(Player.class, player -> touched = true);
        this.platformType = platformType;
    }

    private void render(float dt, Batch batch) {
        if (platformType == PlatformType.FALLING && touched) {
            Body body = components.getInstance(PhysicsComponent.class);
            assert body != null;

            float gravity = Constants.GRAVITY - 0.1f;
            float maxGravity = Constants.MAX_GRAVITY / 2;

            if (body.getVelocity().y > maxGravity) {
                body.getVelocity().y -= gravity;
            }
            body.getPosition().y += body.getVelocity().y * dt;
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }
}
