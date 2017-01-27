package com.brinkman.platformer.spine.utils;

import com.badlogic.gdx.utils.Pool;
import com.brinkman.platformer.spine.Skeleton;
import com.brinkman.platformer.spine.SkeletonData;

/**
 * Created by Austin on 1/26/2017.
 */

public class SkeletonPool extends Pool<Skeleton> {
    private SkeletonData skeletonData;

    public SkeletonPool (SkeletonData skeletonData) {
        this.skeletonData = skeletonData;
    }

    public SkeletonPool (SkeletonData skeletonData, int initialCapacity) {
        super(initialCapacity);
        this.skeletonData = skeletonData;
    }

    public SkeletonPool (SkeletonData skeletonData, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.skeletonData = skeletonData;
    }

    protected Skeleton newObject () {
        return new Skeleton(skeletonData);
    }
}
