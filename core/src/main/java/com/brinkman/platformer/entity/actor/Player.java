package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.action.MeleeAttackAction;
import com.brinkman.platformer.component.action.ActionComponent;
import com.brinkman.platformer.component.action.ActionType;
import com.brinkman.platformer.component.action.SimpleActionComponent;
import com.brinkman.platformer.component.input.InputComponent;
import com.brinkman.platformer.component.input.PlayerInputComponent;
import com.brinkman.platformer.component.physics.ControlledPhysicsComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.render.AnimationRenderComponent;
import com.brinkman.platformer.component.render.AnimationType;
import com.brinkman.platformer.component.render.RenderComponent;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.component.status.SimpleStatusComponent;
import com.brinkman.platformer.component.status.StatusComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.entity.actor.item.Item;
import com.brinkman.platformer.entity.actor.platform.Platform;
import com.brinkman.platformer.physics.*;
import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.brinkman.platformer.component.render.AnimationType.*;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/29/2016.
 */
public class Player extends Actor
{
    private static final Logger LOGGER = new Logger(Player.class.getName(), Logger.DEBUG);

    private TextureAtlas walkRightAtlas;
    private TextureAtlas walkLeftAtlas;
    private TextureAtlas idleRightAtlas;
    private TextureAtlas idleLeftAtlas;
    private TextureAtlas jumpRightAtlas;
    private TextureAtlas jumpLeftAtlas;
    private TextureAtlas meleeRightAtlas;
    private TextureAtlas meleeLeftAtlas;
    private TextureAtlas meleeRightAtlasNormal;
    private TextureAtlas meleeLeftAtlasNormal;
    private TextureAtlas walkRightAtlasNormal;
    private TextureAtlas walkLeftAtlasNormal;
    private TextureAtlas idleRightAtlasNormal;
    private TextureAtlas idleLeftAtlasNormal;
    private TextureAtlas jumpRightAtlasNormal;
    private TextureAtlas jumpLeftAtlasNormal;
    private final Array<Item> inventory;

    private static final float WALK_ANIMATION_TIME = 0.1f;
    private static final float RUN_ANIMATION_TIME = 0.05f;
    private static final float JUMP_ANIMATION_TIME = 0.5f;
    private static final float MELEE_ANIMATION_TIME = 0.1f;
    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT = 64;
    private static final int JUMP_VEL = 12;

    private final Map<AnimationType, Animation<TextureRegion>> animations = new EnumMap<>(AnimationType.class);
    private final Map<AnimationType, Animation<TextureRegion>> normalAnimations = new EnumMap<>(AnimationType.class);
    private final ImmutableClassToInstanceMap<RootComponent> components;

    /**
     * The Player constructor initializes TextureAtlas, Vector2 position, Vector2 velocity, and facingRight.
     */
    public Player() {
        inventory = new Array<>();

        ControlledPhysicsComponent body = new ControlledPhysicsComponent();
        body.setAffectedByGravity(true);
        body.setJumpVelocity(JUMP_VEL);
        body.setWidth(PLAYER_WIDTH * TO_WORLD_UNITS);
        body.setHeight(PLAYER_HEIGHT * TO_WORLD_UNITS);
        Vector2 originPosition = body.getOriginPosition();
        body.getPosition().set(originPosition);

        SimpleStatusComponent statusComponent = new SimpleStatusComponent();
        statusComponent.setMaxHealth(100);
        statusComponent.setCurrentHealth(100);

        CollisionListener<Platform> platformListener = new StaticCollisionListener<>(body);
        CollisionListener<StaticEntity> staticListener = new StaticCollisionListener<>(body);
        // TODO Need to move this out of constructor if possible; leaking potentially uninitialized references
        body.setCollisionListener(Saw.class, new SawCollisionListener(this));
        body.setCollisionListener(Item.class, this::handleItemCollision);
        body.setCollisionListener(Platform.class, platformListener);
        body.setCollisionListener(StaticEntity.class, staticListener);

        initializeTextureAtlas();
        initializeNormalTextureAtlas();

        Map<ActionType, BiConsumer<Entity, GameWorld>> actions = new EnumMap<>(ActionType.class);
        actions.put(ActionType.MELEE_ATTACK, new MeleeAttackAction());

        components = ImmutableClassToInstanceMap.<RootComponent>builder()
              .put(RenderComponent.class, new AnimationRenderComponent(animations.get(IDLE_RIGHT), animations, normalAnimations))
              .put(PhysicsComponent.class, body)
              .put(InputComponent.class, new PlayerInputComponent())
              .put(ActionComponent.class, new SimpleActionComponent(actions))
              .put(StatusComponent.class, statusComponent)
              .build();

        LOGGER.info("Initialized");
    }

    private void handleItemCollision(Item item) { inventory.add(item); }

    /**
     * Initializes TextureAtlas's using Textures from the AssetUtil.ASSET_MANAGER
     */
    private void initializeTextureAtlas() {
        walkRightAtlas = new TextureAtlas("sprites/running/running.pack");
        walkLeftAtlas = new TextureAtlas("sprites/running/running.pack");
        for (AtlasRegion region : walkLeftAtlas.getRegions()) {
            region.flip(true, false);
        }

        idleRightAtlas = new TextureAtlas("sprites/Idle/idle.pack");
        idleLeftAtlas = new TextureAtlas("sprites/Idle/idle.pack");
        for (AtlasRegion region : idleLeftAtlas.getRegions()) {
            region.flip(true, false);
        }

        jumpRightAtlas = new TextureAtlas("sprites/Jump/jump.pack");
        jumpLeftAtlas = new TextureAtlas("sprites/Jump/jump.pack");
        for (AtlasRegion region : jumpLeftAtlas.getRegions()) {
            region.flip(true, false);
        }

        meleeRightAtlas = new TextureAtlas("sprites/melee/melee.pack");
        meleeLeftAtlas = new TextureAtlas("sprites/melee/melee.pack");
        for (AtlasRegion region : meleeLeftAtlas.getRegions()) {
            region.flip(true, false);
        }

        animations.put(IDLE_LEFT, new Animation<>(WALK_ANIMATION_TIME, idleLeftAtlas.getRegions(), PlayMode.LOOP));
        animations.put(IDLE_RIGHT, new Animation<>(WALK_ANIMATION_TIME, idleRightAtlas.getRegions(), PlayMode.LOOP));
        animations.put(WALK_LEFT, new Animation<>(WALK_ANIMATION_TIME, walkLeftAtlas.getRegions(), PlayMode.LOOP));
        animations.put(WALK_RIGHT, new Animation<>(WALK_ANIMATION_TIME, walkRightAtlas.getRegions(), PlayMode.LOOP));
        animations.put(RUN_LEFT, new Animation<>(RUN_ANIMATION_TIME, walkLeftAtlas.getRegions(), PlayMode.LOOP));
        animations.put(RUN_RIGHT, new Animation<>(RUN_ANIMATION_TIME, walkRightAtlas.getRegions(), PlayMode.LOOP));
        animations.put(JUMP_LEFT, new Animation<>(JUMP_ANIMATION_TIME, jumpLeftAtlas.getRegions(), PlayMode.LOOP));
        animations.put(JUMP_RIGHT, new Animation<>(JUMP_ANIMATION_TIME, jumpRightAtlas.getRegions(), PlayMode.LOOP));
        animations.put(MELEE_LEFT, new Animation<>(MELEE_ANIMATION_TIME, meleeLeftAtlas.getRegions(), PlayMode.LOOP));
        animations.put(MELEE_RIGHT, new Animation<>(MELEE_ANIMATION_TIME, meleeRightAtlas.getRegions(), PlayMode.LOOP));

    }

    private void initializeNormalTextureAtlas() {
        walkRightAtlasNormal = new TextureAtlas("sprites/running/normal/running_normal.pack");
        walkLeftAtlasNormal = new TextureAtlas("sprites/running/normal/running_normal.pack");
        for (AtlasRegion region : walkLeftAtlasNormal.getRegions()) {
            region.flip(true, false);
        }

        idleRightAtlasNormal = new TextureAtlas("sprites/Idle/normal/idle_normal.pack");
        idleLeftAtlasNormal = new TextureAtlas("sprites/Idle/normal/idle_normal.pack");
        for (AtlasRegion region : idleLeftAtlasNormal.getRegions()) {
            region.flip(true, false);
        }

        jumpRightAtlasNormal = new TextureAtlas("sprites/Jump/normal/jump_normal.pack");
        jumpLeftAtlasNormal = new TextureAtlas("sprites/Jump/normal/jump_normal.pack");
        for (AtlasRegion region : jumpLeftAtlasNormal.getRegions()) {
            region.flip(true, false);
        }

        meleeRightAtlasNormal = new TextureAtlas("sprites/melee/normal/melee_normal.pack");
        meleeLeftAtlasNormal = new TextureAtlas("sprites/melee/normal/melee_normal.pack");
        for (AtlasRegion region : meleeLeftAtlasNormal.getRegions()) {
            region.flip(true, false);
        }

        normalAnimations.put(IDLE_LEFT, new Animation<>(WALK_ANIMATION_TIME, idleLeftAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(IDLE_RIGHT, new Animation<>(WALK_ANIMATION_TIME, idleRightAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(WALK_LEFT, new Animation<>(WALK_ANIMATION_TIME, walkLeftAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(WALK_RIGHT, new Animation<>(WALK_ANIMATION_TIME, walkRightAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(RUN_LEFT, new Animation<>(RUN_ANIMATION_TIME, walkLeftAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(RUN_RIGHT, new Animation<>(RUN_ANIMATION_TIME, walkRightAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(JUMP_LEFT, new Animation<>(JUMP_ANIMATION_TIME, jumpLeftAtlasNormal.getRegions(), PlayMode.NORMAL));
        normalAnimations.put(JUMP_RIGHT, new Animation<>(JUMP_ANIMATION_TIME, jumpRightAtlasNormal.getRegions(), PlayMode.NORMAL));
        normalAnimations.put(MELEE_RIGHT, new Animation<>(MELEE_ANIMATION_TIME, meleeRightAtlasNormal.getRegions(), PlayMode.NORMAL));
        normalAnimations.put(MELEE_LEFT, new Animation<>(MELEE_ANIMATION_TIME, meleeLeftAtlasNormal.getRegions(), PlayMode.NORMAL));
    }

    /**
     * Returns the HashMap containing inventory currently in the player's 'inventory'. This does not include power-ups
     * or life inventory.
     *
     * @return HashMap Item, ItemType
     */
    public Array<Item> getInventory() { return inventory; }
    
    /**
     * Resets player's position, velocity, and facingRight to their original values. Used when starting a new level.
     */
    public void reset() {
        ControlledBody body = (ControlledBody) components.getInstance(PhysicsComponent.class);
        assert body != null;

        Vector2 originPosition = body.getOriginPosition();
        body.getPosition().set(originPosition);
        body.getVelocity().set(0.0f, 0.0f);
        body.setGrounded(false);
        body.setTouchingLeftWall(false);
        body.setTouchingRightWall(false);
    }

    /**
     * Handles the decrementing, and logic, of player's lives.
     */
    @Override
    public void handleDeath() {
        if (lives > 0) {
            MotileBody body = (MotileBody) components.getInstance(PhysicsComponent.class);
            assert body != null;

            Vector2 originPosition = body.getOriginPosition();
            body.getPosition().set(originPosition);
            body.getVelocity().y = 0;
            // TODO Remove to make game over actually happen
            //    lives--;
        }
    }

    @Override
    public void dispose() {
        idleRightAtlas.dispose();
        idleLeftAtlas.dispose();
        walkRightAtlas.dispose();
        walkLeftAtlas.dispose();
        jumpRightAtlas.dispose();
        jumpLeftAtlas.dispose();

        LOGGER.info("Disposed");
    }

    @Override
    public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }
}

