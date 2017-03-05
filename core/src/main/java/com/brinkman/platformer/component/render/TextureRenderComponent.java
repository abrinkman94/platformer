package com.brinkman.platformer.component.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Caleb Brinkman
 */
public class TextureRenderComponent implements RenderComponent {
    private final TextureRegion textureRegion;

    public TextureRenderComponent(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    @Override
    public TextureRegion getTextureRegion(float deltaT) {
        return textureRegion;
    }

    @Override
    public void setAnimationType(AnimationType animationType) {

    }
}
