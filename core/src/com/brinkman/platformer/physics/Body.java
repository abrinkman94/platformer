package com.brinkman.platformer.physics;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Caleb Brinkman
 */
public interface Body {
    boolean isGrounded();

    void setGrounded(boolean grounded);

    float getMoveSpeed();

    void setMoveSpeed(float moveSpeed);

    Vector2 getVelocity();

    Vector2 getOriginPosition();

    Vector2 getPosition();

    float getHeight();

    void setHeight(float height);

    float getWidth();

    void setWidth(float width);

}
