package com.brinkman.platformer.physics;

import com.badlogic.gdx.math.Vector2;

public class PhysicsBody implements Body {
    private static final float DEFAULT_MOVE_SPEED = 5.0f;

    private final Vector2 originPosition = new Vector2();
    private final Vector2 position = new Vector2();
    private final Vector2 velocity = new Vector2();
    private float width;
    private float height;
    private float moveSpeed = DEFAULT_MOVE_SPEED;
    private boolean grounded;

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
    public Vector2 getPosition() { return position; }

    @Override
    public float getHeight() { return height; }

    @Override
    public void setHeight(float height) { this.height = height; }

    @Override
    public float getWidth() { return width; }

    @Override
    public void setWidth(float width) { this.width = width; }

    @Override
    public Vector2 getOriginPosition() { return originPosition; }
}
