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
import com.brinkman.platformer.component.render.RenderComponent;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.entity.actor.item.Item;
import com.brinkman.platformer.entity.actor.platform.Platform;
import com.brinkman.platformer.physics.*;
import com.brinkman.platformer.util.AssetUtil;
import com.google.common.collect.ImmutableClassToInstanceMap;

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

    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT = 64;
    private static final int IDLE_RIGHT_FRAMES = 0;
    private static final int IDLE_LEFT_FRAMES = 1;
    private static final int WALK_RIGHT_FRAMES = 2;
    private static final int WALK_LEFT_FRAMES = 3;
    private static final int JUMP_RIGHT_FRAMES = 4;
    private static final int JUMP_LEFT_FRAMES = 5;
    private static final int JUMP_VEL = 12;
    private static final float ACCELERATION = 0.8f;

    private boolean left;
    private boolean right;
    private boolean run;

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
		float animationTime = run ? 0.05f : 0.1f;

		switch(currentAnimation) {
			case IDLE_RIGHT_FRAMES:
				animation = new Animation<>(animationTime, idleRightAtlas.getRegions());
				animation.setPlayMode(PlayMode.LOOP);
				break;
			case IDLE_LEFT_FRAMES:
				animation = new Animation<>(animationTime, idleLeftAtlas.getRegions());
				animation.setPlayMode(PlayMode.LOOP);
				break;
			case WALK_RIGHT_FRAMES:
				animation = new Animation<>(animationTime, walkRightAtlas.getRegions());
				animation.setPlayMode(PlayMode.LOOP);
				break;
			case WALK_LEFT_FRAMES:
				animation = new Animation<>(animationTime, walkLeftAtlas.getRegions());
				animation.setPlayMode(PlayMode.LOOP);
				break;
			case JUMP_RIGHT_FRAMES:
				animation = new Animation<>(1, jumpRightAtlas.getRegions());
				animation.setPlayMode(PlayMode.LOOP);
				break;
			case JUMP_LEFT_FRAMES:
				animation = new Animation<>(1, jumpLeftAtlas.getRegions());
				animation.setPlayMode(PlayMode.LOOP);
				break;
			default:
		}

		ControlledBody body = (ControlledBody) components.getInstance(PhysicsComponent.class);
		assert body != null;

		if (left) {
			orientation = "left";
			currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_LEFT_FRAMES : WALK_LEFT_FRAMES;
		} else if (right) {
			orientation = "right";
			currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_RIGHT_FRAMES : WALK_RIGHT_FRAMES;
		} else {
			float xSpeed = body.getVelocity().x;

			if(xSpeed == 0.0f) {
				if ("right".equalsIgnoreCase(orientation)) {
					currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_RIGHT_FRAMES : IDLE_RIGHT_FRAMES;
				} else {
					currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_LEFT_FRAMES : IDLE_LEFT_FRAMES;
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

    //TODO Figure out a way to simplify.
    /**
     * Handles the player's movement logic.
     */
    private void handleMovement() {
        ControlledBody body = (ControlledBody) components.getInstance(PhysicsComponent.class);
        assert body != null;

		//Run conditionals
		float moveSpeed = run ? 10 : 5;
		body.setMoveSpeed(moveSpeed);

		if(left) {
			body.getAcceleration().x = -ACCELERATION;
		} else if (right) {
			body.getAcceleration().x = ACCELERATION;
		}
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
		// TODO Move to... multiple places, probably
		handleMovement();

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

