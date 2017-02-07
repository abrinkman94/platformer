package com.brinkman.platformer.entity.actor.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.Collidable;
import com.brinkman.platformer.util.Constants;

/**
 * Created by Austin on 2/3/2017.
 */
public class Platform  extends Actor{
    private final PlatformType platformType;

    private boolean touched;

    public Platform(float x, float y, float width, float height, PlatformType platformType) {
        getBody().getPosition().set(x, y);
        getBody().setWidth(width);
        getBody().setHeight(height);
        this.platformType = platformType;
    }

    @Override
    public Shape2D getBounds() {
        return new Rectangle(getBody().getPosition().x, getBody().getPosition().y,
                getBody().getWidth(), getBody().getHeight());
    }

    @Override
    public void handleCollisionEvent(Collidable other) {
        if (other instanceof Player) {
            touched = true;
        }
    }

    @Override
    public boolean shouldBeRemovedOnCollision() { return getBody().getPosition().y < (0 - getBody().getHeight()); }

    @Override
    public boolean shouldCollideWith(Collidable other) { return other instanceof Player; }

    @Override
    public void render(float dt, Batch batch) {
        if (platformType == PlatformType.FALLING && touched) {
            float gravity = Constants.GRAVITY - 0.1f;
            float maxGravity = Constants.MAX_GRAVITY / 2;

            if (getBody().getVelocity().y > maxGravity) {
                getBody().getVelocity().y -= gravity;
            }
            getBody().getPosition().y += getBody().getVelocity().y * dt;
        }
    }

    @Override
    public void dispose() {

    }
}
