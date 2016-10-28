package com.brinkman.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.util.Constants;

import static com.brinkman.platformer.util.Constants.GRAVITY;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/29/2016.
 */
public class Player extends Actor {
    private static final Logger LOGGER = new Logger("Player", Logger.DEBUG);

    private final TextureAtlas walkRightAtlas;
    private final TextureAtlas walkLeftAtlas;
    private final TextureAtlas idleRightAtlas;
    private final TextureAtlas idleLeftAtlas;
    private final TextureAtlas jumpRightAtlas;
    private final TextureAtlas jumpLeftAtlas;
    private final Batch batch;
    private Animation animation;

    private static final int IDLE_RIGHT_FRAMES = 0;
    private static final int IDLE_LEFT_FRAMES = 1;
    private static final int WALK_RIGHT_FRAMES = 2;
    private static final int WALK_LEFT_FRAMES = 3;
    private static final int JUMP_RIGHT_FRAMES = 4;
    private static final int JUMP_LEFT_FRAMES = 5;
    private static final int JUMP_VEL = 12;
    private static final float ACCELERATION = 0.5f;
    private static final float DECELERATION = 0.3f;

    private boolean left;
    private boolean right;
    private boolean jump;
    private boolean run;

    /**
     * The Player object. Initializes TextureAtlas, Vector2 position, Vector2 velocity, and orientation.
     * @param batch SpriteBatch
     */
    public Player(Batch batch) {
        this.batch = batch;
        position = new Vector2(originPosition);
        velocity = new Vector2(0, 0);
        orientation = "right";

        walkRightAtlas = new TextureAtlas();
        walkRightAtlas.addRegion("frame1", new TextureRegion(new Texture("sprites/running/frame-1-right.png")));
        walkRightAtlas.addRegion("frame2", new TextureRegion(new Texture("sprites/running/frame-2-right.png")));
        walkRightAtlas.addRegion("frame3", new TextureRegion(new Texture("sprites/running/frame-3-right.png")));
        walkRightAtlas.addRegion("frame4", new TextureRegion(new Texture("sprites/running/frame-4-right.png")));
        walkRightAtlas.addRegion("frame5", new TextureRegion(new Texture("sprites/running/frame-5-right.png")));
        walkRightAtlas.addRegion("frame6", new TextureRegion(new Texture("sprites/running/frame-6-right.png")));

        walkLeftAtlas = new TextureAtlas();
        walkLeftAtlas.addRegion("frame1", new TextureRegion(new Texture("sprites/running/frame-1-left.png")));
        walkLeftAtlas.addRegion("frame2", new TextureRegion(new Texture("sprites/running/frame-2-left.png")));
        walkLeftAtlas.addRegion("frame3", new TextureRegion(new Texture("sprites/running/frame-3-left.png")));
        walkLeftAtlas.addRegion("frame4", new TextureRegion(new Texture("sprites/running/frame-4-left.png")));
        walkLeftAtlas.addRegion("frame5", new TextureRegion(new Texture("sprites/running/frame-5-left.png")));
        walkLeftAtlas.addRegion("frame6", new TextureRegion(new Texture("sprites/running/frame-6-left.png")));

        idleRightAtlas = new TextureAtlas();
        idleRightAtlas.addRegion("frame1", new TextureRegion(new Texture("sprites/idle/frame-1-right.png")));
        idleRightAtlas.addRegion("frame2", new TextureRegion(new Texture("sprites/idle/frame-2-right.png")));

        idleLeftAtlas = new TextureAtlas();
        idleLeftAtlas.addRegion("frame1", new TextureRegion(new Texture("sprites/idle/frame-1-left.png")));
        idleLeftAtlas.addRegion("frame2", new TextureRegion(new Texture("sprites/idle/frame-2-left.png")));


        jumpRightAtlas = new TextureAtlas();
        jumpRightAtlas.addRegion("frame1", new TextureRegion(new Texture("sprites/jump/frame-1-right.png")));

        jumpLeftAtlas = new TextureAtlas();
        jumpLeftAtlas.addRegion("frame1", new TextureRegion(new Texture("sprites/jump/frame-1-left.png")));

        LOGGER.info("Initialized");
    }

    /**
     * Handles the switching of animations.
     */
    private void handleAnimation() {
        switch(currentAnimation) {
            case IDLE_RIGHT_FRAMES:
                animation = new Animation(0.06f, idleRightAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case IDLE_LEFT_FRAMES:
                animation = new Animation(0.06f, idleLeftAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case WALK_RIGHT_FRAMES:
                animation = new Animation(0.06f, walkRightAtlas.getRegions());
                animation.setPlayMode(PlayMode.LOOP);
                break;
            case WALK_LEFT_FRAMES:
                animation = new Animation(0.06f, walkLeftAtlas.getRegions());
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
     * Sets boolean values for input.
     */
    private void setKeyFlags() {
        left = Gdx.input.isKeyPressed(Keys.LEFT);
        right = Gdx.input.isKeyPressed(Keys.RIGHT);
        jump = Gdx.input.isKeyJustPressed(Keys.SPACE);
        run = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT);
    }

    /**
     * Handles the player's movement logic.
     */
    private void handleMovement() {
        setKeyFlags();

        //X-axis movement
        float maxSpeed = moveSpeed;
        float xSpeed = velocity.x;

        if (left &&(xSpeed > -maxSpeed)) {
            xSpeed = xSpeed - ACCELERATION;
            orientation = "left";
            currentAnimation = WALK_LEFT_FRAMES;
        } else if (right &&(xSpeed < maxSpeed)) {
            xSpeed = xSpeed + ACCELERATION;
            orientation = "right";
            currentAnimation = WALK_RIGHT_FRAMES;
        } else {
            if(xSpeed > DECELERATION) {
                xSpeed -= DECELERATION;
            } else if(xSpeed < -DECELERATION) {
                xSpeed += DECELERATION;
            } else {
                if ("right".equalsIgnoreCase(orientation)) {
                    currentAnimation = IDLE_RIGHT_FRAMES;
                } else if ("left".equalsIgnoreCase(orientation)) {
                    currentAnimation = IDLE_LEFT_FRAMES;
                }
                xSpeed = 0;
            }
        }

        //Jump conditionals
        if (jump && canJump) {
            velocity.y = JUMP_VEL;
            grounded = false;
            canJump = false;
        }

        //Run conditionals
        moveSpeed = run ? 8 : 5;

        //Update position and velocity
        velocity.x = xSpeed;
        position.x += velocity.x * Gdx.graphics.getDeltaTime();
        position.y += velocity.y * Gdx.graphics.getDeltaTime();
    }

    /**
     * Resets player's position, velocity, and orientation to their original values. Used when starting a new level.
     */
    public void reset() {
        position = new Vector2(originPosition);
        velocity = new Vector2(moveSpeed, 0);
        orientation = "right";
    }

    /**
     * Handles the decrementing, and logic, of player's lives.
     */
    public void handleDeath() {
        if (lives > 0) {
            position.set(originPosition);
            velocity.y = 0;
            lives--;
        } else {
            Gdx.app.exit();
        }
    }

    @Override
    public void render(float dt) {
        handleAnimation();
        handleMovement();

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
                velocity.y -= GRAVITY;
            }
        }
    }

    @Override
    public void dispose() {
        idleRightAtlas.dispose();
        walkRightAtlas.dispose();
        jumpRightAtlas.dispose();

        LOGGER.info("Disposed");
    }
}
