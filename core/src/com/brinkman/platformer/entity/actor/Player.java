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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.component.PhysicsComponent;
import com.brinkman.platformer.component.RenderComponent;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.entity.actor.item.Item;
import com.brinkman.platformer.entity.actor.platform.Platform;
import com.brinkman.platformer.input.InputFlags;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.Constants;
import com.google.common.collect.ImmutableClassToInstanceMap;

import static com.brinkman.platformer.util.Constants.GRAVITY;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;
import static com.brinkman.platformer.util.TexturePaths.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class Player extends Actor {
    private static final Logger LOGGER = new Logger(Player.class.getName(), Logger.DEBUG);
    /**
     * The Actor's boolean canJump field.
     */
    protected boolean canJump;

    private TextureAtlas walkRightAtlas;
    private TextureAtlas walkLeftAtlas;
    private TextureAtlas idleRightAtlas;
    private TextureAtlas idleLeftAtlas;
    private TextureAtlas jumpRightAtlas;
    private TextureAtlas jumpLeftAtlas;
    private Animation animation;
    private final InputFlags inputFlags;
    private final Array<Item> inventory;
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
    private static final int WALL_BOUNCE = 6;
    private static final float ACCELERATION = 0.8f;
    private static final float DECELERATION = 0.8f;

    private boolean left;
    private boolean right;
    private boolean jump;
    private boolean run;
    private boolean touchingRightWall;
    private boolean touchingLeftWall;
    private boolean justJumped;

    private final ImmutableClassToInstanceMap<RootComponent> components;

    /**
     * The Player constructor initializes TextureAtlas, Vector2 position, Vector2 velocity, and orientation.
     * @param inputFlags InputFlags
     */
    public Player(InputFlags inputFlags) {
        this.inputFlags = inputFlags;
        inventory = new Array<>();


        PhysicsComponent body = new PhysicsComponent();
        body.setAffectedByGravity(true);
        body.setWidth(PLAYER_WIDTH * TO_WORLD_UNITS);
        body.setHeight(PLAYER_HEIGHT * TO_WORLD_UNITS);
        Vector2 originPosition = body.getOriginPosition();
        body.getPosition().set(originPosition);

        body.setCollisionListener(Saw.class, this::handleSawCollision);
        body.setCollisionListener(Item.class, this::handleItemCollision);
        body.setCollisionListener(Platform.class, this::handlePlatformCollision);
        body.setCollisionListener(StaticEntity.class, this::handleStaticCollision);

        orientation = "right";

        initializeTextureAtlas();

        components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .put(RenderComponent.class, this::render)
                .put(PhysicsComponent.class, body)
                .build();

        LOGGER.info("Initialized");
    }

    private void handleSawCollision(Saw saw) { handleDeath(); }

    private void handleItemCollision(Item item) { inventory.add(item); }

    private void handleStaticCollision(StaticEntity staticEntity) { handleStaticCollisions(staticEntity); }

    private void handlePlatformCollision(Platform platform) { handleStaticCollisions(platform); }

    /**
     * Handles collisions with StaticEntity(s) in the Level.
     * @param other Collidable
     */
    private void handleStaticCollisions(Entity other) {
        // Get the centers of the Entity AABB and map AABB; place in tempVector1 and tempVector2 respectively
        Body body = components.getInstance(PhysicsComponent.class);
        Body otherBody = other.getComponents().getInstance(PhysicsComponent.class);
        if ((body != null) && (otherBody != null)) {
            ((Rectangle) body.getBounds()).getCenter(tempVector1);
            ((Rectangle) otherBody.getBounds()).getCenter(tempVector2);
            // Get the absolute value of horizontal overlap between the entity and map tile
            // Save signed value of distance for later
            float horizontalDistance = tempVector2.x - tempVector1.x;
            float entityHalfWidth = ((Rectangle) body.getBounds()).width / 2;
            float mapHalfWidth = ((Rectangle) otherBody.getBounds()).width / 2;
            float horizontalOverlap = (entityHalfWidth + mapHalfWidth) - Math.abs(horizontalDistance);

            // Get the absolute value of the vertical overlap between the entity and the map time
            // Save signed value of distance for later
            float verticalDistance = tempVector2.y - tempVector1.y;
            float entityHalfHeight = ((Rectangle) body.getBounds()).height / 2;
            float mapHalfHeight = ((Rectangle) otherBody.getBounds()).height / 2;
            float verticalOverlap = (entityHalfHeight + mapHalfHeight) - Math.abs(verticalDistance);

            // Move the entity on the axis which has the least overlap.
            // The direction that the entity will move is determined by the sign of the distance between the centers
            Vector2 position = body.getPosition();
            Vector2 velocity = body.getVelocity();

            // Might be temporary solution.  Basically adjusting the overlap so that the Collidable actually overlaps
            // to trigger collision event.
            if (other instanceof Platform) {
                horizontalOverlap = horizontalOverlap * 0.995f;
                verticalOverlap = verticalOverlap * 0.995f;
            }
            if (horizontalOverlap < verticalOverlap) {
                if (horizontalDistance > 0) {
                    touchingRightWall = true;
                    position.x -= horizontalOverlap;
                    velocity.x = 0;
                } else {
                    touchingLeftWall = true;
                    position.x += horizontalOverlap;
                    velocity.x = 0;
                }
                canJump = true;
            } else {
                if (verticalDistance > 0) {
                    position.y -= verticalOverlap;
                    velocity.y -= GRAVITY * 3;
                    canJump = false;
                } else {
                    position.y += verticalOverlap;
                    velocity.y = 0;
                    body.setGrounded(true);
                    canJump = true;
                }
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
                animation = new Animation(animationTime, idleRightAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case IDLE_LEFT_FRAMES:
                animation = new Animation(animationTime, idleLeftAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case WALK_RIGHT_FRAMES:
                animation = new Animation(animationTime, walkRightAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case WALK_LEFT_FRAMES:
                animation = new Animation(animationTime, walkLeftAtlas.getRegions());
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

    //TODO Figure out a way to simplify.
    /**
     * Handles the player's movement logic.
     */
    private void handleMovement() {
        Body body = components.getInstance(PhysicsComponent.class);
        assert body != null;

        setKeyFlags();

        //Run conditionals
        float moveSpeed = run ? 10 : 5;
        body.setMoveSpeed(moveSpeed);

        //X-axis movement
        float maxSpeed = body.getMoveSpeed();
        float xSpeed = body.getVelocity().x;
        boolean movingLeft = xSpeed > -maxSpeed;
        boolean movingRight = xSpeed < maxSpeed;

        if (left && movingLeft) {
            xSpeed = xSpeed - ACCELERATION;
            orientation = "left";
            currentAnimation = (jump && !body.isGrounded()) ? JUMP_LEFT_FRAMES : WALK_LEFT_FRAMES;
        } else if (right && movingRight) {
            xSpeed = xSpeed + ACCELERATION;
            orientation = "right";
            currentAnimation = (jump && !body.isGrounded()) ? JUMP_RIGHT_FRAMES : WALK_RIGHT_FRAMES;
        } else {
            if(xSpeed > DECELERATION) {
                xSpeed -= body.isGrounded() ? DECELERATION : (DECELERATION / 3);
            } else if(xSpeed < -DECELERATION) {
                xSpeed += body.isGrounded() ? DECELERATION : (DECELERATION / 3);
            } else {
                if ("right".equalsIgnoreCase(orientation)) {
                    currentAnimation = (jump && !body.isGrounded()) ? JUMP_RIGHT_FRAMES : IDLE_RIGHT_FRAMES;
                } else {
                    currentAnimation = (jump && !body.isGrounded()) ? JUMP_LEFT_FRAMES : IDLE_LEFT_FRAMES;
                }
                xSpeed = 0;
            }
        }

        //Jump
        // TODO Definitely a candidate for ControlComponent
        xSpeed = handleJump(xSpeed);

        //Update position and velocity
        // TODO This should *probably* be handled in PhysicsComponent
        Vector2 velocity = body.getVelocity();
        Vector2 position = body.getPosition();
        velocity.x = xSpeed;
        position.x += velocity.x * Gdx.graphics.getDeltaTime();
    }

    /**
     * Handles the player's jump logic.
     */
    private float handleJump(float xSpeed) {
        if (jump && canJump && !justJumped) {
            Body body = components.getInstance(PhysicsComponent.class);
            assert body != null;
            Vector2 velocity = body.getVelocity();
            velocity.y = JUMP_VEL;

            //Wall bounce
            if (!body.isGrounded()) {
                if (touchingRightWall) {
                    xSpeed = run ? (velocity.x - (WALL_BOUNCE + 2)) : (velocity.x - WALL_BOUNCE);
                } else if (touchingLeftWall) {
                    xSpeed = run ? (velocity.x + (WALL_BOUNCE + 2)) : (velocity.x + WALL_BOUNCE);
                }
            }
            body.setGrounded(false);
            canJump = false;
            justJumped = true;
        }
        return xSpeed;
    }

    /**
     * Sets player's y velocity based on 'grounded' and 'GRAVITY'.
     */
    private void handleGravity() {
        Body body = components.getInstance(PhysicsComponent.class);
        assert body != null;
        Vector2 velocity = body.getVelocity();
        if (body.isGrounded()) {
            velocity.y = 0;
        } else {
            if (velocity.y > Constants.MAX_GRAVITY) {
                velocity.y -= GRAVITY;
            }
        }
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
        Body body = components.getInstance(PhysicsComponent.class);
        assert body != null;

        Vector2 originPosition = body.getOriginPosition();
        body.getPosition().set(originPosition);
        body.getVelocity().set(0.0f, 0.0f);
        orientation = "right";
        canJump = false;
        body.setGrounded(false);
        touchingLeftWall = false;
        touchingRightWall = false;
    }

    /**
     * Handles the decrementing, and logic, of player's lives.
     */
    @Override
    public void handleDeath() {
        if (lives > 0) {
            Body body = components.getInstance(PhysicsComponent.class);
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
        body.setGrounded(false);
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

    @Override
    public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }
}
