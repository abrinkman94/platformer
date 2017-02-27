package com.brinkman.platformer.physics;

import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.util.Constants;

/**
 * Partial implementation of a Body which is capable of motion.
 */
public abstract class AbstractMotileBody extends AbstractBody implements MotileBody {
    private static final float DEFAULT_MOVE_SPEED = 5.0f;

    private final Vector2 acceleration = new Vector2();
    private final Vector2 velocity = new Vector2();
    private float moveSpeed = DEFAULT_MOVE_SPEED;
    private float maxFallSpeed = Constants.MAX_GRAVITY;
    private float gravityAcceleration = Constants.GRAVITY;
    private boolean grounded;
    private boolean affectedByGravity;

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
}
