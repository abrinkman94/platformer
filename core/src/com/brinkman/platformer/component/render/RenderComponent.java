package com.brinkman.platformer.component.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.physics.MotileBody;

/**
 * Represents a component which can be rendered to the screen.
 *
 * @author Caleb Brinkman
 */
public interface RenderComponent extends RootComponent {
    void render(float dt, Batch batch, Body body);

    TextureRegion getTextureRegion(float deltaT);
}
