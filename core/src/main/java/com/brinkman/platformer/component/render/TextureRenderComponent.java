package com.brinkman.platformer.component.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Caleb Brinkman
 */
public class TextureRenderComponent implements RenderComponent {
    private final TextureRegion textureRegion;
    private TextureRegion normalTextureRegion;

    public TextureRenderComponent(TextureRegion textureRegion, TextureRegion normalTextureRegion) {
        this.textureRegion = textureRegion;
        this.normalTextureRegion = normalTextureRegion;
    }

    public TextureRenderComponent(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }


    @Override
    public TextureRegion getTextureRegion(float deltaT) {
        return textureRegion;
    }

    @Override
    public TextureRegion getNormalTextureRegion(float deltaT) {
        return normalTextureRegion;
    }

    @Override
    public void setAnimationType(AnimationType animationType) {

    }
}
