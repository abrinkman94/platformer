package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.input.InputFlags;
import com.brinkman.platformer.physics.Collidable;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.Constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.brinkman.platformer.util.Constants.GRAVITY;
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
    private ActorState state;
    private final InputFlags inputFlags;
    private final ConcurrentMap<Item, ItemType> items;
    private final Vector2 tempVector1 = new Vector2();
    private final Vector2 tempVector2 = new Vector2();

    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT = 64;
    private static final int IDLE_RIGHT_FRAMES = 0;
    private static final int IDLE_LEFT_FRAMES = 1;
    private static final int WALK_RIGHT_FRAMES = 2;
    private static final int WALK_LEFT_FRAMES = 3;
    private static final int JUMP_RIGHT_FRAMES = 4;
    private static final int JUMP_LEFT_FRAMES = 5;
    private static final int JUMP_VEL = 12;
    private static final int WALL_BOUNCE = 8;
    private static final float ACCELERATION = 0.5f;
    private static final float DECELERATION = 0.3f;
    private static final float ANIMATION_TIME = 0.06f;

    private boolean left;
    private boolean right;
    private boolean jump;
    private boolean run;
    private boolean touchingRightWall;
    private boolean touchingLeftWall;
    private boolean justJumped;

    /**
     * The Player constructor initializes TextureAtlas, Vector2 position, Vector2 velocity, and orientation.
     * @param inputFlags InputFlags
     */
    public Player(InputFlags inputFlags) {
        this.inputFlags = inputFlags;
        width = PLAYER_WIDTH;
        height = PLAYER_HEIGHT;
        position = new Vector2(originPosition);
        velocity = new Vector2(0, 0);
        orientation = "right";
        items = new ConcurrentHashMap<>(8);
        state = ActorState.IDLE;

        initializeTextureAtlas();

        LOGGER.info("Initialized");
    }

    @Override
    public Shape2D getBounds() {
        return new Rectangle(position.x, position.y, width * TO_WORLD_UNITS, height * TO_WORLD_UNITS);
    }

    @Override
    public boolean shouldBeRemovedOnCollision() {
        return lives < 0;
    }

    @Override
    public void handleCollisionEvent(Collidable other) {
        if(other instanceof Saw) {
            handleDeath();
        } else if(other instanceof Item) {
            Item item = (Item) other;
            if (ItemType.LIFE == item.getItemType()) {
                setLives(getLives() + 1);
            } else if (ItemType.KEY == item.getItemType()) {
                getItems().put(item, ItemType.KEY);
            }
        } else if (other instanceof StaticEntity) {
            handleStaticCollisions(other);
        }
    }

    /**
     * Handles collisions with StaticEntity(s) in the Level.
     * @param other Collidable
     */
    private void handleStaticCollisions(Collidable other) {
        // Get the centers of the Entity AABB and map AABB; place in tempVector1 and tempVector2 respectively
        ((Rectangle)getBounds()).getCenter(tempVector1);
        ((Rectangle)other.getBounds()).getCenter(tempVector2);
        // Get the absolute value of horizontal overlap between the entity and map tile
        // Save signed value of distance for later
        float horizontalDistance = tempVector2.x - tempVector1.x;
        float entityHalfWidth = ((Rectangle)getBounds()).width / 2;
        float mapHalfWidth = ((Rectangle)other.getBounds()).width / 2;
        float horizontalOverlap = (entityHalfWidth + mapHalfWidth) - Math.abs(horizontalDistance);

        // Get the absolute value of the vertical overlap between the entity and the map time
        // Save signed value of distance for later
        float verticalDistance = tempVector2.y - tempVector1.y;
        float entityHalfHeight = ((Rectangle)getBounds()).height / 2;
        float mapHalfHeight = ((Rectangle)other.getBounds()).height / 2;
        float verticalOverlap = (entityHalfHeight + mapHalfHeight) - Math.abs(verticalDistance);

        // Move the entity on the axis which has the least overlap.
        // The direction that the entity will move is determined by the sign of the distance between the centers
        if (horizontalOverlap < verticalOverlap) {
            if (horizontalDistance > 0) {
                touchingRightWall = true;
                position.x = position.x - horizontalOverlap;
                velocity.x = 0;
            } else {
                touchingLeftWall = true;
                position.x = position.x + horizontalOverlap;
                velocity.x = 0;
            }
            canJump = true;
        } else {
            if (verticalDistance > 0) {
                position.y = position.y - verticalOverlap;
                velocity.y = -(position.y - GRAVITY);
                canJump = false;
            } else {
                position.y = position.y + verticalOverlap;
                velocity.y = 0;
                grounded = true;
                canJump = true;
            }
        }
    }

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
     * Returns the HashMap containing items currently in the player's 'inventory'. This does not include power-ups or
     * life items.
     * @return HashMap Item, ItemType
     */
    public Map<Item, ItemType> getItems() { return items; }

    /**
     * Handles the switching of animations.
     */
    private void handleAnimation() {
        switch(currentAnimation) {
            case IDLE_RIGHT_FRAMES:
                animation = new Animation(ANIMATION_TIME, idleRightAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case IDLE_LEFT_FRAMES:
                animation = new Animation(ANIMATION_TIME, idleLeftAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case WALK_RIGHT_FRAMES:
                animation = new Animation(ANIMATION_TIME, walkRightAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case WALK_LEFT_FRAMES:
                animation = new Animation(ANIMATION_TIME, walkLeftAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case JUMP_RIGHT_FRAMES:
                animation = new Animation(1, jumpRightAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case JUMP_LEFT_FRAMES:
                animation = new Animation(1, jumpLeftAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            default:
        }
    }

    /**
     * Sets boolean values for input from InputFlags.
     */
    private void setKeyFlags() {
        left = inputFlags.left();
        right = inputFlags.right();
        jump = inputFlags.jump();
        run = inputFlags.run();
    }

    /**
     * Returns the current enum ActorState of the player.
     * @return ActorState
     */
    public ActorState getState() { return state; }

    /**
     * Sets player states.
     */
    private void handleStates() {
        //JUMPING state
        if (velocity.y > 0) {
            state = ActorState.JUMPING;
        }

        //FALLING state
        if (velocity.y < -0.5) {
            state = ActorState.FALLING;
        }

        //GROUNDED state
        if (grounded) {
            state = ActorState.GROUNDED;
        }

        //IDLE state
        if (velocity.x == 0 && velocity.y == 0) {
            state = ActorState.IDLE;
        }
    }

    //TODO Figure out a way to simplify.
    /**
     * Handles the player's movement logic.
     */
    private void handleMovement() {
        setKeyFlags();

        //X-axis movement
        float maxSpeed = moveSpeed;
        float xSpeed = velocity.x;
        boolean runningLeft = xSpeed > -maxSpeed;
        boolean runningRight = xSpeed < maxSpeed;

        if (left && runningLeft) {
            xSpeed = xSpeed - ACCELERATION;
            orientation = "left";
            currentAnimation = (jump && !grounded) ? JUMP_LEFT_FRAMES : WALK_LEFT_FRAMES;
        } else if (right && runningRight) {
            xSpeed = xSpeed + ACCELERATION;
            orientation = "right";
            currentAnimation = (jump && !grounded) ? JUMP_RIGHT_FRAMES : WALK_RIGHT_FRAMES;
        } else {
            if(xSpeed > DECELERATION) {
                xSpeed -= grounded ? DECELERATION : (DECELERATION / 3);
            } else if(xSpeed < -DECELERATION) {
                xSpeed += grounded ? DECELERATION : (DECELERATION / 3);
            } else {
                if ("right".equalsIgnoreCase(orientation)) {
                    currentAnimation = (jump && !grounded) ? JUMP_RIGHT_FRAMES : IDLE_RIGHT_FRAMES;
                } else {
                    currentAnimation = (jump && !grounded) ? JUMP_LEFT_FRAMES : IDLE_LEFT_FRAMES;
                }
                xSpeed = 0;
            }
        }

        //Jump conditionals
        if (jump && canJump && !justJumped) {
            velocity.y = JUMP_VEL;

            //Wall bounce
            if (state != ActorState.GROUNDED) {
                if (touchingRightWall) {
                    xSpeed = run ? (xSpeed - (WALL_BOUNCE + 1)) : (xSpeed - WALL_BOUNCE);
                } else if (touchingLeftWall) {
                    xSpeed = run ? (xSpeed + (WALL_BOUNCE + 1)) : (xSpeed + WALL_BOUNCE);
                }
            }
            grounded = false;
            canJump = false;
            justJumped = true;
        }


        //Run conditionals
        moveSpeed = run ? 10 : 6;

        //Update position and velocity
        velocity.x = xSpeed;
        position.x += velocity.x * Gdx.graphics.getDeltaTime();
        position.y += velocity.y * Gdx.graphics.getDeltaTime();
    }

    /**
     * Sets boolean justJumped.  True if the player just jumped, else false.
     * @param justJumped boolean
     */
    public void setJustJumped(boolean justJumped) { this.justJumped = justJumped; }

    /**
     * Resets player's position, velocity, and orientation to their original values. Used when starting a new level.
     */
    public void reset() {
        position = new Vector2(originPosition);
        velocity = new Vector2(0, 0);
        orientation = "right";
        canJump = false;
    }

    /**
     * Handles the decrementing, and logic, of player's lives.
     */
    @Override
    public void handleDeath() {
        if (lives > 0) {
            position.set(originPosition);
            velocity.y = 0;
            // TODO Remove to make game over actually happen
        //    lives--;
        }
    }

    @Override
    public void render(float dt, Batch batch) {
        handleAnimation();
        handleMovement();
        handleStates();

        elapsedTime += Gdx.graphics.getDeltaTime();

        batch.begin();
        batch.draw(animation.getKeyFrame(elapsedTime, false), position.x, position.y, width * TO_WORLD_UNITS,
                height * TO_WORLD_UNITS);
        batch.end();

        //Checks if player is on the ground
        if (grounded) {
            velocity.y = 0;
        } else {
            if (velocity.y > Constants.MAX_GRAVITY) {
                // Slight decrease in Gravity when running to allow for more air control.
                velocity.y -= run ? (GRAVITY * 0.95f) : GRAVITY;
            }
        }

        //Handle player falling off map
        if ((position.x < 0) || (position.y < 0)){
            handleDeath();
        }

        grounded = false;
        canJump = false;
        touchingLeftWall = false;
        touchingRightWall = false;
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
}
