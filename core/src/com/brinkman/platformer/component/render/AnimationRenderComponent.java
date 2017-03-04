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
    private final Animation<TextureRegion> defaultAnimation;
    private final Map<AnimationType, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private float elapsedTime;

    public AnimationRenderComponent(Animation<TextureRegion> defaultAnimation,
                                    Map<AnimationType, Animation<TextureRegion>> animations) {
        this.defaultAnimation = defaultAnimation;
        this.animations = animations;
        this.currentAnimation = defaultAnimation;
    }

    public AnimationRenderComponent(Animation<TextureRegion> defaultAnimation) {
        this(defaultAnimation, new EnumMap<>(AnimationType.class));
    }

    @Override
    public void render(float dt, Batch batch, Body body) {
        TextureRegion currentFrame = getTextureRegion(dt);
        Vector2 position = body.getPosition();
        float width = body.getWidth();
        float height = body.getHeight();

        batch.begin();
        batch.draw(currentFrame, position.x, position.y, width, height);
        batch.end();
    }

    @Override
    public TextureRegion getTextureRegion(float dt) {
        elapsedTime += dt;

        return currentAnimation.getKeyFrame(elapsedTime, true);
    }

    @Override
    public void setAnimationType(AnimationType animationType) {
        Animation<TextureRegion> animationTemp = animations.get(animationType);

        currentAnimation = (animationTemp != null) ? animationTemp : defaultAnimation;
    }
}
