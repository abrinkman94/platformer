package com.brinkman.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.mappings.Xbox;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.util.ControllerMappings;

import static com.brinkman.platformer.util.Constants.GRAVITY;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/29/2016.
 */
public class Player extends Actor {
    private static Logger LOGGER = new Logger("Player", Logger.DEBUG);

    private TextureAtlas walk_right_atlas;
    private TextureAtlas walk_left_atlas;
    private TextureAtlas idle_right_atlas;
    private TextureAtlas idle_left_atlas;
    private TextureAtlas jump_right_atlas;
    private TextureAtlas jump_left_atlas;
    private Batch batch;
    private Animation animation;
    private Vector2 acceleration;
    private Vector2 deceleration;
    private Controller controller;

    private static final int IDLE_RIGHT_FRAMES = 0;
    private static final int IDLE_LEFT_FRAMES = 1;
    private static final int WALK_RIGHT_FRAMES = 2;
    private static final int WALK_LEFT_FRAMES = 3;
    private static final int JUMP_RIGHT_FRAMES = 4;
    private static final int JUMP_LEFT_FRAMES = 5;
    private boolean left, right, jump, run;
    private boolean jumpKeyPressed;

    public Player(Batch batch) {
        this.batch = batch;
        position = new Vector2(originPosition);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0.75f, 0);
        deceleration = new Vector2(0.5f, 0);
        controller = Controllers.getControllers().first();
        orientation = "right";

        walk_right_atlas = new TextureAtlas();
        walk_right_atlas.addRegion("frame1", new TextureRegion(new Texture("sprites/running/frame-1-right.png")));
        walk_right_atlas.addRegion("frame2", new TextureRegion(new Texture("sprites/running/frame-2-right.png")));
        walk_right_atlas.addRegion("frame3", new TextureRegion(new Texture("sprites/running/frame-3-right.png")));
        walk_right_atlas.addRegion("frame4", new TextureRegion(new Texture("sprites/running/frame-4-right.png")));
        walk_right_atlas.addRegion("frame5", new TextureRegion(new Texture("sprites/running/frame-5-right.png")));
        walk_right_atlas.addRegion("frame6", new TextureRegion(new Texture("sprites/running/frame-6-right.png")));

        walk_left_atlas = new TextureAtlas();
        walk_left_atlas.addRegion("frame1", new TextureRegion(new Texture("sprites/running/frame-1-left.png")));
        walk_left_atlas.addRegion("frame2", new TextureRegion(new Texture("sprites/running/frame-2-left.png")));
        walk_left_atlas.addRegion("frame3", new TextureRegion(new Texture("sprites/running/frame-3-left.png")));
        walk_left_atlas.addRegion("frame4", new TextureRegion(new Texture("sprites/running/frame-4-left.png")));
        walk_left_atlas.addRegion("frame5", new TextureRegion(new Texture("sprites/running/frame-5-left.png")));
        walk_left_atlas.addRegion("frame6", new TextureRegion(new Texture("sprites/running/frame-6-left.png")));

        idle_right_atlas = new TextureAtlas();
        idle_right_atlas.addRegion("frame1", new TextureRegion(new Texture("sprites/idle/frame-1-right.png")));
        idle_right_atlas.addRegion("frame2", new TextureRegion(new Texture("sprites/idle/frame-2-right.png")));

        idle_left_atlas = new TextureAtlas();
        idle_left_atlas.addRegion("frame1", new TextureRegion(new Texture("sprites/idle/frame-1-left.png")));
        idle_left_atlas.addRegion("frame2", new TextureRegion(new Texture("sprites/idle/frame-2-left.png")));


        jump_right_atlas = new TextureAtlas();
        jump_right_atlas.addRegion("frame1", new TextureRegion(new Texture("sprites/jump/frame-1-right.png")));

        jump_left_atlas = new TextureAtlas();
        jump_left_atlas.addRegion("frame1", new TextureRegion(new Texture("sprites/jump/frame-1-left.png")));

        LOGGER.info("Initialized");
    }

    private void checkAnimation() {
        if (currentAnimation == IDLE_RIGHT_FRAMES) {
            animation = new Animation(1/15f, idle_right_atlas.getRegions());
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } else if (currentAnimation == IDLE_LEFT_FRAMES) {
            animation = new Animation(1/15f, idle_left_atlas.getRegions());
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } else if (currentAnimation == WALK_RIGHT_FRAMES) {
            animation = new Animation(1/15f, walk_right_atlas.getRegions());
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } else if (currentAnimation == WALK_LEFT_FRAMES) {
            animation = new Animation(1/15f, walk_left_atlas.getRegions());
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } else if (currentAnimation == JUMP_RIGHT_FRAMES) {
            animation = new Animation(1f, jump_right_atlas.getRegions());
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } else if (currentAnimation == JUMP_LEFT_FRAMES) {
            animation = new Animation(1f, jump_left_atlas.getRegions());
            animation.setPlayMode(Animation.PlayMode.LOOP);
        }
    }

    private void handleMovement() {
        left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || controller.getAxis(ControllerMappings.AXIS_LEFT_X) == -1;
        right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || controller.getAxis(ControllerMappings.AXIS_LEFT_X) == 1;
        jump = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || controller.getButton(ControllerMappings.BUTTON_A);
        run = controller.getButton(ControllerMappings.BUTTON_LB) || controller.getButton(ControllerMappings.BUTTON_RB);

        float maxSpeed = moveSpeed;
        float xSpeed = velocity.x;

        if (left &&(xSpeed > -maxSpeed)) {
            xSpeed = xSpeed - acceleration.x;
            orientation = "left";
            currentAnimation = WALK_LEFT_FRAMES;
        } else if (right &&(xSpeed < maxSpeed)) {
            xSpeed = xSpeed + acceleration.x;
            orientation = "right";
            currentAnimation = WALK_RIGHT_FRAMES;
        } else {
            if(xSpeed > deceleration.x) {
                xSpeed -= deceleration.x;
            } else if(xSpeed < -deceleration.x) {
                xSpeed += deceleration.x;
            } else {
                if (orientation.equalsIgnoreCase("right")) {
                    currentAnimation = IDLE_RIGHT_FRAMES;
                } else if (orientation.equalsIgnoreCase("left")) {
                    currentAnimation = IDLE_LEFT_FRAMES;
                }
                xSpeed = 0;
            }
        }

        if (jump && canJump) {
            velocity.y = 12;
            grounded = false;
            canJump = false;
        }

        if (run) {
            moveSpeed = 8;
        } else {
            moveSpeed = 5;
        }

        velocity.x = xSpeed;
        position.x += velocity.x * Gdx.graphics.getDeltaTime();
        position.y += velocity.y * Gdx.graphics.getDeltaTime();
    }


    public void collectCoin(Array<Coin> coins) {
        Rectangle playerBounds = getBounds();

        for (Coin coin : coins) {
            Rectangle coinBounds = new Rectangle(coin.getPosition().x, coin.getPosition().y,
                    coin.getWidth(), coin.getHeight());

            if (playerBounds.overlaps(coinBounds)) {
                coin.setIsCollected(true);
                coins.removeValue(coin, true);
            }
        }
    }

    public void reset() {
        position = new Vector2(originPosition);
        velocity = new Vector2(moveSpeed, 0);
        orientation = "right";
    }

    @Override
    public void onDeath() {
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
        checkAnimation();

        batch.begin();
        elapsedTime += Gdx.graphics.getDeltaTime();
        batch.draw(animation.getKeyFrame(elapsedTime, false), position.x, position.y, width * TO_WORLD_UNITS,
                height * TO_WORLD_UNITS);
        batch.end();

        if (grounded) {
            velocity.y = 0;
        } else {
            velocity.y -= GRAVITY;
        }

        handleMovement();
    }

    @Override
    public void dispose() {
        idle_right_atlas.dispose();
        walk_right_atlas.dispose();
        jump_right_atlas.dispose();

        LOGGER.info("Disposed");
    }
}
