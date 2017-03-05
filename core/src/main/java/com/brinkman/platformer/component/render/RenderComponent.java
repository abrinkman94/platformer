package com.brinkman.platformer.component.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brinkman.platformer.component.RootComponent;

/**
 * Represents a component which can be rendered to the screen.
 *
 * @author Caleb Brinkman
 */
public interface RenderComponent extends RootComponent
{

    TextureRegion getTextureRegion(float deltaT);

    void setAnimationType(AnimationType animationType);
}
