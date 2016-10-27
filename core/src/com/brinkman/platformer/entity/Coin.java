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
    private static Logger LOGGER = new Logger("Coin", Logger.DEBUG);

    private Batch batch;
    private Animation animations;

    private boolean isCollected = false;
    private float elapsedTime = 0f;

    public Coin(Batch batch, float x, float y) {
        this.batch = batch;
        position = new Vector2(x, y);
        width = 32 * TO_WORLD_UNITS;
        height = 32 * TO_WORLD_UNITS;

        texture = new Texture("sprites/coinsheet.png");

        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth()/8, texture.getHeight()/8);

        TextureRegion[] textureRegions = new TextureRegion[8 * 8];
        int index = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                textureRegions[index++] = tmp[i][j];
            }
        }
        animations = new Animation(0.025f, textureRegions);
        elapsedTime = 0f;
    }

    void setIsCollected(boolean isCollected) { this.isCollected = isCollected; }

    @Override
    public void onDeath() {

    }

    @Override
    public void render(float dt) {
        if (!isCollected) {
            elapsedTime += dt;
            TextureRegion currentFrame = animations.getKeyFrame(elapsedTime, true);
            batch.begin();
            batch.draw(currentFrame, position.x, position.y, 1, 1);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        LOGGER.info("Disposed");
    }
}
