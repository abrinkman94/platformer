package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/29/2016.
 */
public abstract class Actor implements Entity{

    protected Sprite sprite;
    protected Texture texture;
    protected Vector2 position;
    protected Vector2 originPosition = new Vector2(2, 2);
    protected Vector2 velocity;
    protected float width = 32;
    protected float height = 64;
    protected float moveSpeed = 5 * TO_WORLD_UNITS;
    protected float elapsedTime;
    protected int lives = 3;
    protected int currentAnimation;
    protected boolean grounded = false;
    protected boolean canJump = false;
    protected String orientation;

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width * TO_WORLD_UNITS, height * TO_WORLD_UNITS);
    }

    public Sprite getSprite() { return sprite; }

    public Vector2 getPosition() { return position; }

    public Vector2 getVelocity() { return velocity; }

    public float getWidth() { return width; }

    public float getHeight() { return height; }

    public int getLives() { return lives; }

    public void setLives(int lives) { this.lives = lives; }

    public boolean isGrounded() { return grounded; }

    public void setIsGrounded(boolean grounded) { this.grounded = grounded; }

    public boolean canJump() { return canJump; }

    public void setCanJump(boolean canJump) { this.canJump = canJump; }

    public String getOrientation() { return orientation; }

    public void setOrientation(String orientation) { this.orientation = orientation; }
}
