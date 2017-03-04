package com.brinkman.platformer.component.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.physics.Body;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Caleb Brinkman
 */
public class AnimationRenderComponent implements RenderComponent
{
    private final Animation<TextureRegion> animation;
    private final Map<AnimationType, Animation<TextureRegion>> animations;
    private float elapsedTime;

    public AnimationRenderComponent(Animation<TextureRegion> animation,
                                    Map<AnimationType, Animation<TextureRegion>> animations) {
        this.animation = animation;
        this.animations = animations;
    }

    public AnimationRenderComponent(Animation<TextureRegion> animation) {
        this(animation, new EnumMap<>(AnimationType.class));
    }

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
