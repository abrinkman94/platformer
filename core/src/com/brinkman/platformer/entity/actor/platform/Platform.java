package com.brinkman.platformer.entity.actor.platform;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.Collidable;

/**
 * Created by Austin on 2/3/2017.
 */
public class Platform  extends Actor{
    private final PlatformType platformType;

    private boolean touched = false;
    private float fallVelocity = 0;

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
    public boolean shouldCollideWith(Collidable other) { return other instanceof Player; }

    @Override
    public void render(float dt, Batch batch) {
        if (platformType == PlatformType.FALLING && touched) {
            fallVelocity += 0.002f;
            getBody().getPosition().y -= fallVelocity;
        }
    }

    @Override
    public void dispose() {

    }
}
