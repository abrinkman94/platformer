package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Austin on 9/29/2016.
 */
public interface Entity {


    /**
     * Returns the Rectangle bounds of entity.
     * @return Rectangle
     */
    Rectangle getBounds();

    /**
     * Handles the logic for 'death'.
     */
    void handleDeath();

    /**
     * Handles the updating and rendering of the Entity object.
     * @param dt float delta time
     */
    void render(float dt, Batch batch);

    /**
     * Handles the disposing of textures, etc...
     */
    void dispose();
}
