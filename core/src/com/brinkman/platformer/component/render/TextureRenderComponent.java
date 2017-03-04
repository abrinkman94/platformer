package com.brinkman.platformer.component.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.physics.Body;

/**
 * @author Caleb Brinkman
 */
public class TextureRenderComponent implements RenderComponent {
    private final TextureRegion textureRegion;

    public TextureRenderComponent(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    @Override
    public void render(float dt, Batch batch, Body body) {
        float height = body.getHeight();
        float width = body.getWidth();
        Vector2 position = body.getPosition();

        batch.begin();
        batch.draw(textureRegion, position.x, position.y, width, height);
        batch.end();
    }

    @Override
    public TextureRegion getTextureRegion(float deltaT) {
        return textureRegion;
    }

    @Override
    public void setAnimationType(AnimationType animationType) {

    }
}
