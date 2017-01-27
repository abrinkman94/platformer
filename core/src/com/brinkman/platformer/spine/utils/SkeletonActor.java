package com.brinkman.platformer.spine.utils;

/**
 * Created by Austin on 1/26/2017.
 */

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.brinkman.platformer.spine.AnimationState;
import com.brinkman.platformer.spine.Skeleton;
import com.brinkman.platformer.spine.SkeletonRenderer;

/** A scene2d actor that draws a skeleton. */
public class SkeletonActor extends Actor {
    private SkeletonRenderer renderer;
    private Skeleton skeleton;
    AnimationState state;

    /** Creates an uninitialized SkeletonActor. The renderer, skeleton, and animation state must be set before use. */
    public SkeletonActor () {
    }

    public SkeletonActor (SkeletonRenderer renderer, Skeleton skeleton, AnimationState state) {
        this.renderer = renderer;
        this.skeleton = skeleton;
        this.state = state;
    }

    public void act (float delta) {
        state.update(delta);
        state.apply(skeleton);
        skeleton.updateWorldTransform();
        super.act(delta);
    }

    public void draw (Batch batch, float parentAlpha) {
        Color color = skeleton.getColor();
        float oldAlpha = color.a;
        skeleton.getColor().a *= parentAlpha;

        skeleton.setPosition(getX(), getY());
        renderer.draw(batch, skeleton);

        color.a = oldAlpha;
    }

    public SkeletonRenderer getRenderer () {
        return renderer;
    }

    public void setRenderer (SkeletonRenderer renderer) {
        this.renderer = renderer;
    }

    public Skeleton getSkeleton () {
        return skeleton;
    }

    public void setSkeleton (Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    public AnimationState getAnimationState () {
        return state;
    }

    public void setAnimationState (AnimationState state) {
        this.state = state;
    }
}
