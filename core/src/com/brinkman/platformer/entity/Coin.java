package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 10/1/2016.
 */
public class Coin extends Actor {
    private static final Logger LOGGER = new Logger("Coin", Logger.DEBUG);

    private final Batch batch;
    private final Animation animations;
    private final TextureRegion[][] tmp;
    private final TextureRegion[] textureRegions;

    private static final float ANIMATION_TIME = 0.025f;

    private boolean collected;

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
        width = 64 * TO_WORLD_UNITS;
        height = 64 * TO_WORLD_UNITS;

        texture = new Texture("sprites/coinsheet.png");

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

    /**
     * Sets the value for boolean collected.
     * @param collected boolean
     */
    public void setCollected(boolean collected) { this.collected = collected; }

    @Override
    public void render(float dt) {
        if (!collected) {
            elapsedTime += dt;
            TextureRegion currentFrame = animations.getKeyFrame(elapsedTime, true);
            batch.begin();
            batch.draw(currentFrame, position.x, position.y, 1, 1);
            batch.end();
        }
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
