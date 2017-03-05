package com.brinkman.platformer.component.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
