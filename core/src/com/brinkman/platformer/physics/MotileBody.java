package com.brinkman.platformer.physics;

import com.badlogic.gdx.math.*;

/**
 * @author Caleb Brinkman
 */
public interface MotileBody extends Body {
    boolean isGrounded();

    void setGrounded(boolean grounded);

    float getMoveSpeed();

    void setMoveSpeed(float moveSpeed);

    Vector2 getVelocity();

    Vector2 getOriginPosition();

    float getMaxFallSpeed();

    void setMaxFallSpeed(float maxFallSpeed);

    boolean isAffectedByGravity();

    void setAffectedByGravity(boolean affectedByGravity);

    float getGravityAcceleration();

    Vector2 getAcceleration();

    void setGravityAcceleration(float gravityAcceleration);

    boolean isJumping();

    void setJumping(boolean jumping);

    boolean justJumped();

    void setJustJumped(boolean justJumped);

    float getJumpVelocity();

    void setJumpVelocity(float jumpVelocity);

    boolean isTouchingRightWall();

    void setTouchingRightWall(boolean touchingRightWall);

    boolean isTouchingLeftWall();

    void setTouchingLeftWall(boolean touchingLeftWall);

}
