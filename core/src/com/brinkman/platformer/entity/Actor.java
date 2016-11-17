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

    /**
     * The Actor's Sprite.
     */
    protected Sprite sprite;
    /**
     * The Actor's Texture.
     */
    protected Texture texture;
    /**
     * The Actor's Vector2 position.
     */
    protected Vector2 position;
    /**
     * The Actor's Vector2 originPosition.
     */
    protected final Vector2 originPosition = new Vector2(2, 11);
    /**
     * The Actor's Vector2 velocity.
     */
    protected Vector2 velocity;
    /**
     * The Actor's float width.
     */
    protected float width;
    /**
     * The Actor's float height.
     */
    protected float height;
    /**
     * The Actor's float moveSpeed.
     */
    protected float moveSpeed = 7;
    /**
     * The Actor's float elapsedTime, used for animations.
     */
    protected float elapsedTime;
    /**
     * The Actor's int lives.
     */
    protected int lives = 3;
    /**
     * The Actor's int currentAnimation, used for animations.
     */
    protected int currentAnimation;
    /**
     * The Actor's boolean grounded field.
     */
    protected boolean grounded;
    /**
     * The Actor's boolean canJump field.
     */
    protected boolean canJump;
    /**
     * The Actor's String orientation.
     */
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
