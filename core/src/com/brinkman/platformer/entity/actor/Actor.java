package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.brinkman.platformer.component.render.AnimationType;
import com.brinkman.platformer.entity.Entity;

/**
 * Created by Austin on 9/29/2016.
 */
public abstract class Actor implements Entity {
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
    protected AnimationType currentAnimation = AnimationType.IDLE_RIGHT;

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


}
