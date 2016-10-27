package com.brinkman.platformer.entity;

/**
 * Created by Austin on 9/29/2016.
 */
public interface Entity {

    void onDeath();
    /**
     * Handles the updating and rendering of the Entity object.
     * @param dt float delta time
     */
    void render(float dt);

    /**
     * Handles the disposing of textures, etc...
     */
    void dispose();
}
