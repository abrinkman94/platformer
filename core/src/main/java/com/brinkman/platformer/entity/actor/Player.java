package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.component.input.InputComponent;
import com.brinkman.platformer.component.input.PlayerInputComponent;
import com.brinkman.platformer.component.physics.ControlledPhysicsComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.render.AnimationRenderComponent;
import com.brinkman.platformer.component.render.AnimationType;
import com.brinkman.platformer.component.render.RenderComponent;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.entity.actor.item.Item;
import com.brinkman.platformer.entity.actor.platform.Platform;
import com.brinkman.platformer.physics.*;
import com.brinkman.platformer.util.AssetUtil;
import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.EnumMap;
import java.util.Map;

import static com.brinkman.platformer.component.render.AnimationType.*;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;
import static com.brinkman.platformer.util.TexturePaths.*;

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
    private TextureAtlas walkRightAtlasNormal;
    private TextureAtlas walkLeftAtlasNormal;
    private TextureAtlas idleRightAtlasNormal;
    private TextureAtlas idleLeftAtlasNormal;
    private TextureAtlas jumpRightAtlasNormal;
    private TextureAtlas jumpLeftAtlasNormal;
    private final Array<Item> inventory;

    private static final float WALK_ANIMATION_TIME = 0.1f;
    private static final float RUN_ANIMATION_TIME = 0.05f;
    private static final float JUMP_ANIMATION_TIME = 1.0f;
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

        CollisionListener<Platform> platformListener = new StaticCollisionListener<>(body);
        CollisionListener<StaticEntity> staticListener = new StaticCollisionListener<>(body);
        // TODO Need to move this out of constructor if possible; leaking potentially uninitialized references
        body.setCollisionListener(Saw.class, new SawCollisionListener(this));
        body.setCollisionListener(Item.class, this::handleItemCollision);
        body.setCollisionListener(Platform.class, platformListener);
        body.setCollisionListener(StaticEntity.class, staticListener);

        initializeTextureAtlas();
        initializeNormalTextureAtlas();

        components = ImmutableClassToInstanceMap.<RootComponent>builder()
              .put(RenderComponent.class, new AnimationRenderComponent(animations.get(IDLE_RIGHT), animations, normalAnimations))
              .put(PhysicsComponent.class, body)
              .put(InputComponent.class, new PlayerInputComponent())
              .build();

        LOGGER.info("Initialized");
    }

    private void handleItemCollision(Item item) { inventory.add(item); }

    /**
     * Initializes TextureAtlas's using Textures from the AssetUtil.ASSET_MANAGER
     */
    private void initializeTextureAtlas() {
        walkRightAtlas = new TextureAtlas();
        walkRightAtlas.addRegion("frame1",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_1_RIGHT, Texture.class)));
        walkRightAtlas.addRegion("frame2",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_2_RIGHT, Texture.class)));
        walkRightAtlas.addRegion("frame3",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_3_RIGHT, Texture.class)));
        walkRightAtlas.addRegion("frame4",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_4_RIGHT, Texture.class)));
        walkRightAtlas.addRegion("frame5",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_5_RIGHT, Texture.class)));
        walkRightAtlas.addRegion("frame6",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_6_RIGHT, Texture.class)));

        walkLeftAtlas = new TextureAtlas();
        for (AtlasRegion region : walkRightAtlas.getRegions()) {
            int i = 0;
            i++;
            walkLeftAtlas.addRegion("frame " + i, new TextureRegion(region.getTexture())).flip(true, false);
        }

        idleRightAtlas = new TextureAtlas();
        idleRightAtlas.addRegion("frame1",
              new TextureRegion((Texture) AssetUtil.getAsset(IDLE_FRAME_1_RIGHT, Texture.class)));
        idleRightAtlas.addRegion("frame2",
              new TextureRegion((Texture) AssetUtil.getAsset(IDLE_FRAME_2_RIGHT, Texture.class)));

        idleLeftAtlas = new TextureAtlas();
        for (AtlasRegion region : idleRightAtlas.getRegions()) {
            int i = 0;
            i++;
            idleLeftAtlas.addRegion("frame" + i, new TextureRegion(region.getTexture())).flip(true, false);
        }

        jumpRightAtlas = new TextureAtlas();
        jumpRightAtlas.addRegion("frame1",
              new TextureRegion((Texture) AssetUtil.getAsset(JUMP_FRAME_1_RIGHT, Texture.class)));

        jumpLeftAtlas = new TextureAtlas();
        for (AtlasRegion region : jumpRightAtlas.getRegions()) {
            int i = 0;
            i++;
            jumpLeftAtlas.addRegion("frame" + i, new TextureRegion(region.getTexture())).flip(true, false);
        }

        animations.put(IDLE_LEFT, new Animation<>(WALK_ANIMATION_TIME, idleLeftAtlas.getRegions(), PlayMode.LOOP));
        animations.put(IDLE_RIGHT, new Animation<>(WALK_ANIMATION_TIME, idleRightAtlas.getRegions(), PlayMode.LOOP));
        animations.put(WALK_LEFT, new Animation<>(WALK_ANIMATION_TIME, walkLeftAtlas.getRegions(), PlayMode.LOOP));
        animations.put(WALK_RIGHT, new Animation<>(WALK_ANIMATION_TIME, walkRightAtlas.getRegions(), PlayMode.LOOP));
        animations.put(RUN_LEFT, new Animation<>(RUN_ANIMATION_TIME, walkLeftAtlas.getRegions(), PlayMode.LOOP));
        animations.put(RUN_RIGHT, new Animation<>(RUN_ANIMATION_TIME, walkRightAtlas.getRegions(), PlayMode.LOOP));
        animations.put(JUMP_LEFT, new Animation<>(JUMP_ANIMATION_TIME, jumpLeftAtlas.getRegions(), PlayMode.LOOP));
        animations.put(JUMP_RIGHT, new Animation<>(JUMP_ANIMATION_TIME, jumpRightAtlas.getRegions(), PlayMode.LOOP));

    }

    private void initializeNormalTextureAtlas() {
        walkRightAtlasNormal = new TextureAtlas();
        walkRightAtlasNormal.addRegion("frame1",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_1_RIGHT, Texture.class)));
        walkRightAtlasNormal.addRegion("frame2",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_2_RIGHT, Texture.class)));
        walkRightAtlasNormal.addRegion("frame3",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_3_RIGHT, Texture.class)));
        walkRightAtlasNormal.addRegion("frame4",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_4_RIGHT, Texture.class)));
        walkRightAtlasNormal.addRegion("frame5",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_5_RIGHT, Texture.class)));
        walkRightAtlasNormal.addRegion("frame6",
              new TextureRegion((Texture) AssetUtil.getAsset(RUN_FRAME_6_RIGHT, Texture.class)));

        walkLeftAtlasNormal = new TextureAtlas();
        for (AtlasRegion region : walkRightAtlasNormal.getRegions()) {
            int i = 0;
            i++;
            walkLeftAtlasNormal.addRegion("frame " + i, new TextureRegion(region.getTexture())).flip(true, false);
        }

        idleRightAtlasNormal = new TextureAtlas();
        idleRightAtlasNormal.addRegion("frame1",
              new TextureRegion((Texture) AssetUtil.getAsset(IDLE_FRAME_1_RIGHT, Texture.class)));
        idleRightAtlasNormal.addRegion("frame2",
              new TextureRegion((Texture) AssetUtil.getAsset(IDLE_FRAME_2_RIGHT, Texture.class)));

        idleLeftAtlasNormal = new TextureAtlas();
        for (AtlasRegion region : idleRightAtlas.getRegions()) {
            int i = 0;
            i++;
            idleLeftAtlasNormal.addRegion("frame" + i, new TextureRegion(region.getTexture())).flip(true, false);
        }

        jumpRightAtlasNormal = new TextureAtlas();
        jumpRightAtlasNormal.addRegion("frame1",
              new TextureRegion((Texture) AssetUtil.getAsset(JUMP_FRAME_1_RIGHT, Texture.class)));

        jumpLeftAtlasNormal = new TextureAtlas();
        for (AtlasRegion region : jumpRightAtlasNormal.getRegions()) {
            int i = 0;
            i++;
            jumpLeftAtlasNormal.addRegion("frame" + i, new TextureRegion(region.getTexture())).flip(true, false);
        }

        normalAnimations.put(IDLE_LEFT, new Animation<>(WALK_ANIMATION_TIME, idleLeftAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(IDLE_RIGHT, new Animation<>(WALK_ANIMATION_TIME, idleRightAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(WALK_LEFT, new Animation<>(WALK_ANIMATION_TIME, walkLeftAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(WALK_RIGHT, new Animation<>(WALK_ANIMATION_TIME, walkRightAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(RUN_LEFT, new Animation<>(RUN_ANIMATION_TIME, walkLeftAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(RUN_RIGHT, new Animation<>(RUN_ANIMATION_TIME, walkRightAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(JUMP_LEFT, new Animation<>(JUMP_ANIMATION_TIME, jumpLeftAtlasNormal.getRegions(), PlayMode.LOOP));
        normalAnimations.put(JUMP_RIGHT, new Animation<>(JUMP_ANIMATION_TIME, jumpRightAtlasNormal.getRegions(), PlayMode.LOOP));
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

