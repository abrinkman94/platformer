package com.brinkman.platformer.component.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.physics.Body;

/**
 * @author Caleb Brinkman
 */
public class AnimationRenderComponent implements RenderComponent {
    private final Animation<TextureRegion> animation;
    private float elapsedTime;

    public AnimationRenderComponent(Animation<TextureRegion> animation) {this.animation = animation;}

    @Override
    public void render(float dt, Batch batch, Body body) {
        elapsedTime += dt;

        TextureRegion currentFrame = animation.getKeyFrame(elapsedTime, true);
        Vector2 position = body.getPosition();
        float width = body.getWidth();
        float height = body.getHeight();

        batch.begin();
        batch.draw(currentFrame, position.x, position.y, width, height);
        batch.end();
    }
}
