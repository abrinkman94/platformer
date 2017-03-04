package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.brinkman.platformer.component.RootComponent;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * Created by Austin on 9/29/2016.
 */
public interface Entity {
    /**
     * Handles the disposing of textures, etc...
     */
    void dispose();

    ImmutableClassToInstanceMap<RootComponent> getComponents();

}
