package com.brinkman.platformer.physics;

/**
 * Listens for (and responds to) collision events.
 *
 * @author Caleb Brinkman
 */
public interface CollisionListener<T> {
    /**
     * Called on collision with a body.
     *
     * @param other The other body.
     */
    void onCollision(T other);
}
