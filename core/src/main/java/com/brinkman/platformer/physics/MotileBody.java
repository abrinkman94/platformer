package com.brinkman.platformer.physics;

import com.badlogic.gdx.math.Vector2;

/**
 * @author Caleb Brinkman
 */
public interface MotileBody extends Body {
    boolean isGrounded();

    void setGrounded(boolean grounded);

    float getMoveSpeed();

    void setMoveSpeed(float moveSpeed);

    Vector2 getVelocity();

    float getMaxFallSpeed();

    void setMaxFallSpeed(float maxFallSpeed);

    boolean isAffectedByGravity();

    void setAffectedByGravity(boolean affectedByGravity);

    float getGravityAcceleration();

    Vector2 getAcceleration();

    void setGravityAcceleration(float gravityAcceleration);
}
