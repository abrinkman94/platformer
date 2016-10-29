package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/29/2016.
 */
public abstract class Actor implements Entity {

    protected Sprite sprite;
    protected Texture texture;
    protected Vector2 position;
    protected Vector2 originPosition = new Vector2(2, 2);
    protected Vector2 velocity;
    protected float width;
    protected float height;
    protected float moveSpeed = 5;
    protected float elapsedTime;
    protected int lives = 3;
    protected int currentAnimation;
    protected boolean grounded;
    protected boolean canJump;
    protected String orientation;

    /**
     * Returns a new Rectangle, holding the bounds of the Actor.
     * @return Rectangle
     */
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width * TO_WORLD_UNITS, height * TO_WORLD_UNITS);
    }

    /**
     * .
     * @return Sprite
     */
    public Sprite getSprite() { return sprite; }

    /**
     * Returns the Vector2 position.
     * @return Vector2
     */
    public Vector2 getPosition() { return position; }

    /**
     * Returns the Vector2 velocity.
     * @return Vector2
     */
    public Vector2 getVelocity() { return velocity; }

    /**
     * Returns the float width.
     * @return float
     */
    public float getWidth() { return width; }

    /**
     * Returns the float height.
     * @return float
     */
    public float getHeight() { return height; }

    /**
     * Returns the int lives.
     * @return int
     */
    public int getLives() { return lives; }

    /**
     * Sets the value of int lives.
     * @param lives int
     */
    public void setLives(int lives) { this.lives = lives; }

    /**
     * Returns the value of boolean grounded.
     * @return boolean
     */
    public boolean isGrounded() { return grounded; }

    /**
     * Sets the value of boolean grounded.
     * @param grounded boolean
     */
    public void setIsGrounded(boolean grounded) { this.grounded = grounded; }

    /**
     * Returns the value of boolean canJump.
     * @return boolean
     */
    public boolean canJump() { return canJump; }

    /**
     * Sets the value of boolean canJump.
     * @param canJump boolean
     */
    public void setCanJump(boolean canJump) { this.canJump = canJump; }

    /**
     * Returns the value of String orientation.
     * @return String
     */
    public String getOrientation() { return orientation; }

    /**
     * Sets the value of String orientation.
     * @param orientation String
     */
    public void setOrientation(String orientation) { this.orientation = orientation; }
}
