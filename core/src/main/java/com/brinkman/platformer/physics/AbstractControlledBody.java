package com.brinkman.platformer.physics;

/**
 * @author Caleb Brinkman
 */
public class AbstractControlledBody extends AbstractMotileBody implements ControlledBody {
    private boolean jumping;
    private boolean justJumped;
    private float jumpVelocity;
    private boolean touchingRightWall;
    private boolean touchingLeftWall;
    private boolean facingRight = true;

    @Override
    public boolean isJumping() { return jumping; }

    @Override
    public void setJumping(boolean jumping) { this.jumping = jumping; }

    @Override
    public boolean justJumped() { return justJumped; }

    @Override
    public void setJustJumped(boolean justJumped) { this.justJumped = justJumped; }

    @Override
    public float getJumpVelocity() { return jumpVelocity; }

    @Override
    public void setJumpVelocity(float jumpVelocity) { this.jumpVelocity = jumpVelocity; }

    @Override
    public boolean isTouchingRightWall() { return touchingRightWall; }

    @Override
    public void setTouchingRightWall(boolean touchingRightWall) { this.touchingRightWall = touchingRightWall; }

    @Override
    public boolean isTouchingLeftWall() { return touchingLeftWall; }

    @Override
    public void setTouchingLeftWall(boolean touchingLeftWall) { this.touchingLeftWall = touchingLeftWall; }

    @Override
    public boolean isFacingRight() {
        return facingRight;
    }

    @Override
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
}
