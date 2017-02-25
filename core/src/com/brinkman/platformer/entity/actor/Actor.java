package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.brinkman.platformer.entity.Entity;

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
     * The Actor's String orientation.
     */
    protected String orientation;

    /**
     * Optional method to handle death of Actor.
     */
    public void handleDeath() {};

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
