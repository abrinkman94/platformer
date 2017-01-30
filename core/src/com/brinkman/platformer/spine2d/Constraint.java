package com.brinkman.platformer.spine2d;

/**
 * Created by Austin on 1/26/2017.
 */
/** The interface for all constraints. */
public interface Constraint extends Updatable {
    /** The ordinal for the order a skeleton's constraints will be applied. */
    public int getOrder ();
}
