package com.brinkman.platformer.component;

import com.brinkman.platformer.physics.AbstractMotileBody;
import com.brinkman.platformer.physics.ControlledBody;

/**
 * @author Caleb Brinkman
 */
public class ControlledPhysicsComponent extends AbstractMotileBody implements ControlledBody, PhysicsComponent {
    private boolean jumping;
    private boolean justJumped;
    private float jumpVelocity;
    private boolean touchingRightWall;
    private boolean touchingLeftWall;

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
}
