package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.physics.Collidable;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.TexturePaths;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 10/1/2016.
 */
public class Coin extends Actor {
    private static final Logger LOGGER = new Logger("Coin", Logger.DEBUG);

    private final Animation animations;
    private final TextureRegion[][] tmp;
    private final TextureRegion[] textureRegions;

    private static final float ANIMATION_TIME = 0.025f;
    private static final int COIN_SIZE = 64;

    /**
     * Constructs the Coin object.
     * @param x float x position
     * @param y float y position
     */
    public Coin(float x, float y) {
        elapsedTime = 0;
        position = new Vector2(x, y);
        width = COIN_SIZE * TO_WORLD_UNITS;
        height = COIN_SIZE  * TO_WORLD_UNITS;

        texture = (Texture) AssetUtil.getAsset(TexturePaths.COIN_SPRITESHEET, Texture.class);

        tmp = TextureRegion.split(texture, texture.getWidth()/8, texture.getHeight()/8);

        textureRegions = new TextureRegion[8 * 8];
        int index = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                textureRegions[index++] = tmp[i][j];
            }
        }

        animations = new Animation(ANIMATION_TIME, textureRegions);
    }

    @Override
    public Shape2D getBounds() {
        return new Circle(position.x + (width / 2),
              position.y, width * 0.5f);
    }

    @Override
    public void handleCollisionEvent(GameWorld world) {
        Entity player = world.getEntityByValue("player");
        Rectangle playerBounds = (Rectangle) player.getBounds();
        boolean isCollected = Intersector.overlaps((Circle) getBounds(), playerBounds);

        if (isCollected) {
            world.getCoins().removeValue(this, true);

            animations.setFrameDuration(0.002f);

            if (getWidth() > 0.1f) {
                animateCollect(-0.05f);
            }

            Timer timer = new Timer();
            timer.scheduleTask(new Task() {
                @Override
                public void run() {
                    removeCoinFromWorld(world);
                }
            }, .30f);
        }
    }

    private void removeCoinFromWorld(GameWorld world) {
        world.removeEntity(this);
    }

    private void animateCollect(float increment) {
        width += increment;
        height += increment;
    }

    @Override
    public void render(float dt, Batch batch) {
        elapsedTime += dt;
        TextureRegion currentFrame = animations.getKeyFrame(elapsedTime, true);
        batch.begin();
        batch.draw(currentFrame, position.x, position.y, width, height);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tmp[i][j].getTexture().dispose();
            }
        }

        for (int i = 0; i < 64; i++) {
            textureRegions[i].getTexture().dispose();
        }
    }
}