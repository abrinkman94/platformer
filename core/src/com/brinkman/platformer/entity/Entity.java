package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.physics.Collidable;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * Created by Austin on 9/29/2016.
 */
public interface Entity extends Collidable {
    /**
     * Handles the updating and rendering of the Entity object.
     * @param dt float delta time
     * @param batch SpriteBatch
     */
    void render(float dt, Batch batch);

    /**
     * Handles the disposing of textures, etc...
     */
    void dispose();

    ImmutableClassToInstanceMap<RootComponent> getComponents();
}
