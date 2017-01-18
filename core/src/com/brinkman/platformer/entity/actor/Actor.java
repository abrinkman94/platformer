package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.physics.PhysicsBody;

/**
 * Created by Austin on 9/29/2016.
 */
public abstract class Actor implements Entity {
    private final Body body = new PhysicsBody();

    /**
     * The Actor's Sprite.
     */
    protected Sprite sprite;
    /**
     * The Actor's Texture.
     */
    protected Texture texture;
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
     * Optional method to handle death of Actor.
     */
    public void handleDeath() {};

    /**
     * .
     * @return Sprite
     */
    public Sprite getSprite() { return sprite; }

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

    public Body getBody() { return body; }
}
