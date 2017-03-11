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
    private final Map<AnimationType, Animation<TextureRegion>> normalAnimations;
    private final Animation<TextureRegion> defaultNormal;
    private Animation<TextureRegion> currentAnimation;
    private Animation<TextureRegion> currentNormalAnimation;
    private float elapsedTimeColor;
    private float elapsedTimeNormal;

    public AnimationRenderComponent(Animation<TextureRegion> defaultAnimation,
                                    Animation<TextureRegion> defaultNormal,
                                    Map<AnimationType, Animation<TextureRegion>> animations,
                                    Map<AnimationType, Animation<TextureRegion>> normalAnimations) {
        this.defaultAnimation = defaultAnimation;
        this.defaultNormal = defaultNormal;
        this.animations = animations;
        this.normalAnimations = normalAnimations;
        this.currentAnimation = defaultAnimation;
        this.currentNormalAnimation = defaultNormal;
    }

    public AnimationRenderComponent(Animation<TextureRegion> defaultAnimation) {
        this(defaultAnimation, null, new EnumMap<>(AnimationType.class), new EnumMap<>(AnimationType.class));
    }

    @Override
    public TextureRegion getTextureRegion(float deltaT) {
        elapsedTimeColor += deltaT;

        return currentAnimation.getKeyFrame(elapsedTimeColor, true);
    }

    @Override
    public void setAnimationType(AnimationType animationType) {
        Animation<TextureRegion> animationTemp = animations.get(animationType);
        Animation<TextureRegion> normalAnimationTemp = normalAnimations.get(animationType);

        currentAnimation = (animationTemp != null) ? animationTemp : defaultAnimation;
        currentNormalAnimation = (normalAnimationTemp != null) ? normalAnimationTemp : defaultNormal;
    }

    @Override
    public TextureRegion getNormalTextureRegion(float deltaT) {
        elapsedTimeNormal += deltaT;

        return currentNormalAnimation.getKeyFrame(elapsedTimeNormal, true);
    }
}
