package com.brinkman.platformer.physics;

/**
 * @author Caleb Brinkman
 */
public interface ControlledBody extends MotileBody {

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

    boolean isFacingRight();

    void setFacingRight(boolean facingRight);
}
