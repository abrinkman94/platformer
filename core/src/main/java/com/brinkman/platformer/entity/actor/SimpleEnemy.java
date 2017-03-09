package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.component.input.InputComponent;
import com.brinkman.platformer.component.input.PlayerInputComponent;
import com.brinkman.platformer.component.physics.ControlledPhysicsComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.render.AnimationRenderComponent;
import com.brinkman.platformer.component.render.AnimationType;
import com.brinkman.platformer.component.render.RenderComponent;
import com.brinkman.platformer.component.render.TextureRenderComponent;
import com.brinkman.platformer.component.status.SimpleStatusComponent;
import com.brinkman.platformer.component.status.StatusComponent;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.entity.actor.platform.Platform;
import com.brinkman.platformer.physics.CollisionListener;
import com.brinkman.platformer.physics.StaticCollisionListener;
import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.EnumMap;
import java.util.Map;

import static com.brinkman.platformer.component.render.AnimationType.IDLE_LEFT;
import static com.brinkman.platformer.component.render.AnimationType.IDLE_RIGHT;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * @author Austin Brinkman.
 */
public class SimpleEnemy extends Actor
{
    private final ImmutableClassToInstanceMap<RootComponent> components;
    private final Map<AnimationType, Animation<TextureRegion>> animations = new EnumMap<>(AnimationType.class);
    private final Map<AnimationType, Animation<TextureRegion>> normalAnimations = new EnumMap<>(AnimationType.class);
    private final TextureAtlas idleAtlas;
    private final TextureAtlas idleAtlasNormal;

    public SimpleEnemy() {
        idleAtlas = new TextureAtlas("sprites/test-sprite/idle/idle.pack");
        animations.put(IDLE_LEFT, new Animation<>(0.1f, idleAtlas.getRegions(), Animation.PlayMode.LOOP));

        idleAtlasNormal = new TextureAtlas("sprites/test-sprite/idle/normal/idle.pack");
        normalAnimations.put(IDLE_LEFT, new Animation<>(0.1f, idleAtlasNormal.getRegions(), Animation.PlayMode.LOOP));

        ControlledPhysicsComponent body = new ControlledPhysicsComponent();
        body.setAffectedByGravity(true);
        body.setWidth(32 * TO_WORLD_UNITS);
        body.setHeight(64 * TO_WORLD_UNITS);
        Vector2 originPosition = body.getOriginPosition();
        body.getPosition().set(originPosition.x + 2, originPosition.y);

        SimpleStatusComponent statusComponent = new SimpleStatusComponent();
        statusComponent.setMaxHealth(100);
        statusComponent.setCurrentHealth(100);

        CollisionListener<Platform> platformListener = new StaticCollisionListener<>(body);
        CollisionListener<StaticEntity> staticListener = new StaticCollisionListener<>(body);
        body.setCollisionListener(Platform.class, platformListener);
        body.setCollisionListener(StaticEntity.class, staticListener);

        components = ImmutableClassToInstanceMap.<RootComponent> builder()
              .put(RenderComponent.class, new AnimationRenderComponent(animations.get(IDLE_LEFT), animations, normalAnimations))
              .put(PhysicsComponent.class, body)
              .put(StatusComponent.class, statusComponent)
              .build();
    }

    @Override
    public void handleDeath() {}

    @Override
    public void dispose() {

    }

    @Override
    public ImmutableClassToInstanceMap<RootComponent> getComponents() {
        return components;
    }
}
