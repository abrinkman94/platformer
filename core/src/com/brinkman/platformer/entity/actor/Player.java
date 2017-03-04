package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.component.InputComponent;
import com.brinkman.platformer.component.physics.ControlledPhysicsComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
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
public class Player extends Actor {
    private static final Logger LOGGER = new Logger(Player.class.getName(), Logger.DEBUG);

    private TextureAtlas walkRightAtlas;
    private TextureAtlas walkLeftAtlas;
    private TextureAtlas idleRightAtlas;
    private TextureAtlas idleLeftAtlas;
    private TextureAtlas jumpRightAtlas;
    private TextureAtlas jumpLeftAtlas;
    private Animation animation;
    private final Array<Item> inventory;

    private static final float WALK_ANIMATION_TIME = 0.1f;
    private static final float RUN_ANIMATION_TIME = 0.05f;
    private static final float JUMP_ANIMATION_TIME = 1.0f;
    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT = 64;
    private static final int JUMP_VEL = 12;

    private boolean left;
    private boolean right;
    private boolean run;

    private final Map<AnimationType, Animation<TextureRegion>> animations = new EnumMap<>(AnimationType.class);
	private final ImmutableClassToInstanceMap<RootComponent> components;

	/**
	 * The Player constructor initializes TextureAtlas, Vector2 position, Vector2 velocity, and orientation.
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

		orientation = "right";

		initializeTextureAtlas();

        components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .put(RenderComponent.class, this::render)
                .put(PhysicsComponent.class, body)
			  	.put(InputComponent.class, this::setKeyFlags)
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

	/**
	 * Returns the HashMap containing inventory currently in the player's 'inventory'. This does not include power-ups or
	 * life inventory.
	 * @return HashMap Item, ItemType
	 */
	public Array<Item> getInventory() { return inventory; }

	/**
	 * Handles the switching of animations.
	 */
	private void handleAnimationSwitching() {

		animation = animations.get(currentAnimation);

		ControlledBody body = (ControlledBody) components.getInstance(PhysicsComponent.class);
		assert body != null;

		if (left) {
			orientation = "left";
			currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_LEFT : WALK_LEFT;
		} else if (right) {
			orientation = "right";
			currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_RIGHT : WALK_RIGHT;
		} else {
			float xSpeed = body.getVelocity().x;

			if(xSpeed == 0.0f) {
				if ("right".equalsIgnoreCase(orientation)) {
					currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_RIGHT : IDLE_RIGHT;
				} else {
					currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_LEFT : IDLE_LEFT;
				}
			}
		}
	}

    /**
     * Sets boolean values for input from InputFlags.
     */
    private void setKeyFlags(boolean left, boolean right, boolean run) {
        this.left = left;
        this.right = right;
        this.run = run;
    }


	/**
     * Resets player's position, velocity, and orientation to their original values. Used when starting a new level.
     */
    public void reset() {
        ControlledBody body = (ControlledBody) components.getInstance(PhysicsComponent.class);
        assert body != null;

        Vector2 originPosition = body.getOriginPosition();
        body.getPosition().set(originPosition);
        body.getVelocity().set(0.0f, 0.0f);
        orientation = "right";
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

	private void render(float dt, Batch batch, Body body) {
		// TODO Move to render... somehow.  Maybe render needs a control component as well?
		handleAnimationSwitching();

		elapsedTime += Gdx.graphics.getDeltaTime();

		Vector2 position = body.getPosition();
		TextureRegion frame = (TextureRegion) animation.getKeyFrame(elapsedTime, false);
		float width = body.getWidth();
		float height = body.getHeight();

		batch.begin();
		batch.draw(frame, position.x, position.y, width, height);
		batch.end();

		//Handle player falling off map
		// TODO Move to... maybe MechanicsComponent? Some sort of component to handle game rules, anyway...
		if ((position.x < 0) || ((position.y + body.getHeight()) < 0)){
			handleDeath();
		}

        // TODO Probably move to PhysicsComponent, but unsure; maybe need to check in ControlComponent or something.
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

