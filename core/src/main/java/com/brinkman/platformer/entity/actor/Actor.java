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


}
