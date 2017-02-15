package com.brinkman.platformer.physics;

import com.badlogic.gdx.math.*;

/**
 * @author Caleb Brinkman
 */
public interface Body {
    Vector2 getPosition();

    float getHeight();

    void setHeight(float height);

    float getWidth();

    void setWidth(float width);

    boolean isRemovedOnCollision();

    void setRemovedOnCollision(boolean removedOnCollision);

    /**
     * Add a collision listener to this Body, which will listen to and respond to collision events with the given type
     * of object.
     *
     * @param otherType The type of object against which to listen for collisions.
     * @param listener  The listener.
     */
    <T> void setCollisionListener(Class<T> otherType, CollisionListener<T> listener);

    /**
     * Return whether this body should collide with one owned by the specified object.
     *
     * @param otherObject The other object.
     * @param <T>         The type of object.
     *
     * @return Whether this body should collide with the object specified.
     */
    <T> boolean shouldCollideWith(T otherObject);

    /**
     * Trigger any collision listeners listening for collisions with the specified object.
     *
     * @param otherObject The object for which to trigger collision listeners.
     * @param otherBody   The other body.
     * @param <T>         The type of body for which to trigger a collision listener.
     */
    <T> void triggerCollisionListeners(T otherObject, Body otherBody);

    default Shape2D getBounds() {
        float height = getHeight();
        float width = getWidth();
        Vector2 position = getPosition();
        return new Rectangle(position.x, position.y, width, height);
    }

    default boolean intersects(Body other) {
        boolean intersects = false;
        Shape2D bounds = getBounds();
        Shape2D otherBounds = other.getBounds();
        if (bounds instanceof Circle) {
            if (otherBounds instanceof Circle) {
                intersects = Intersector.overlaps((Circle) bounds, (Circle) otherBounds);
            } else if (otherBounds instanceof Rectangle) {
                intersects = Intersector.overlaps((Circle) bounds, (Rectangle) otherBounds);
            }
        } else if (bounds instanceof Rectangle) {
            if (otherBounds instanceof Circle) {
                intersects = Intersector.overlaps((Circle) otherBounds, (Rectangle) bounds);
            } else if (otherBounds instanceof Rectangle) {
                intersects = Intersector.overlaps((Rectangle) bounds, (Rectangle) otherBounds);
            }
        }
        return intersects;
    }
}
