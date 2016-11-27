package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.TexturePaths;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 10/1/2016.
 */
public class Coin extends Actor {
    private static final Logger LOGGER = new Logger("Coin", Logger.DEBUG);

    private final Batch batch;
    private Animation animations;
    private final TextureRegion[][] tmp;
    private final TextureRegion[] textureRegions;

    private boolean isCollected;
    private static final float ANIMATION_TIME = 0.025f;
    private static final int COIN_SIZE = 64;

    /**
     * Constructs the Coin object.
     * @param batch SpriteBatch
     * @param x float x position
     * @param y float y position
     */
    public Coin(Batch batch, float x, float y) {
        this.batch = batch;
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

    public Circle getCircleBounds() { return new Circle(position.x + (width / 2),
            position.y, width * 0.5f); }

    public void setIsCollected(boolean isCollected) { this.isCollected = isCollected; }

    public boolean isCollected() { return isCollected; }

    public Animation getAnimations() { return animations; }

    public void animateCollect(float width, float height, float increment) {
        width += increment;
        height += increment;

        this.width = width;
        this.height = height;
    }

    @Override
    public void handleDeath() {}

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
