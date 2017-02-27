package com.brinkman.platformer.component;

import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.physics.AbstractBody;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.physics.CollisionListener;
import com.brinkman.platformer.physics.ControlledBody;
import com.brinkman.platformer.util.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Caleb Brinkman
 */
public class ControlledPhysicsComponent extends AbstractBody implements ControlledBody, PhysicsComponent {
    private static final float DEFAULT_MOVE_SPEED = 5.0f;

    private final Vector2 acceleration = new Vector2();
    private final Vector2 velocity = new Vector2();
    private float moveSpeed = DEFAULT_MOVE_SPEED;
    private boolean grounded;
    private float maxFallSpeed = Constants.MAX_GRAVITY;
    private float gravityAcceleration = Constants.GRAVITY;
    private boolean affectedByGravity;
    private boolean jumping;
    private boolean justJumped;
    private float jumpVelocity;
    private boolean touchingRightWall;
    private boolean touchingLeftWall;

    @Override
    public boolean isGrounded() { return grounded; }

    @Override
    public void setGrounded(boolean grounded) { this.grounded = grounded; }

    @Override
    public float getMoveSpeed() { return moveSpeed; }

    @Override
    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }

    @Override
    public Vector2 getVelocity() { return velocity; }

    @Override
    public float getMaxFallSpeed() { return maxFallSpeed; }

    @Override
    public void setMaxFallSpeed(float maxFallSpeed) { this.maxFallSpeed = maxFallSpeed; }

    @Override
    public boolean isAffectedByGravity() { return affectedByGravity;}

    @Override
    public void setAffectedByGravity(boolean affectedByGravity) { this.affectedByGravity = affectedByGravity; }

    @Override
    public float getGravityAcceleration() { return gravityAcceleration; }

    @Override
    public Vector2 getAcceleration() { return acceleration; }

    @Override
    public void setGravityAcceleration(float gravityAcceleration) { this.gravityAcceleration = gravityAcceleration; }

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
