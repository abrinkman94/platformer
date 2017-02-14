package com.brinkman.platformer.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.brinkman.platformer.physics.Body;

/**
 * Represents a component which can be rendered to the screen.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface RenderComponent extends RootComponent {
    void render(float dt, Batch batch, Body body);
}
