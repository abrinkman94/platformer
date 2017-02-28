package com.brinkman.platformer.entity.actor.platform;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brinkman.platformer.component.*;
import com.brinkman.platformer.component.physics.ControlledPhysicsComponent;
import com.brinkman.platformer.component.physics.MotilePhysicsComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.util.Constants;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * Created by Austin on 2/3/2017.
 */
public class Platform  extends Actor{

    private final ImmutableClassToInstanceMap<RootComponent> components;

    public Platform(TextureRegion texture, float x, float y, float width, float height, PlatformType platformType) {

        MotilePhysicsComponent body = new MotilePhysicsComponent();

        body.setAffectedByGravity(platformType == PlatformType.FALLING);
        body.setMaxFallSpeed(Constants.MAX_GRAVITY / 2);
        body.setGravityAcceleration(0.0f);
        body.getPosition().set(x, y);
        body.setWidth(width);
        body.setHeight(height);
        body.setCollisionListener(Player.class, player -> {
            body.setGravityAcceleration(Constants.GRAVITY - 0.1f);
        });

        components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .put(RenderComponent.class, new TextureRenderComponent(texture))
                .put(PhysicsComponent.class, body)
                .build();
    }

    @Override
    public void dispose() {

    }

    @Override
    public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }
}
