package com.brinkman.platformer.physics;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Caleb Brinkman
 */
public abstract class AbstractBody implements Body {
    private final Map<Class<?>, CollisionListener<?>> collisionListeners = new HashMap<>(4);
    private final Vector2 originPosition = new Vector2(2, 6);
    private final Vector2 position = new Vector2();
    private float width;
    private float height;
    private boolean removedOnCollision;

    @Override
    public Vector2 getPosition() { return position; }

    @Override
    public float getHeight() { return height; }

    @Override
    public void setHeight(float height) { this.height = height; }

    @Override
    public float getWidth() { return width; }

    @Override
    public void setWidth(float width) { this.width = width; }

    @Override
    public boolean isRemovedOnCollision() { return removedOnCollision; }

    @Override
    public void setRemovedOnCollision(boolean removedOnCollision) { this.removedOnCollision = removedOnCollision; }

    @Override
    public Vector2 getOriginPosition() { return originPosition; }

    @Override
    public <T> void setCollisionListener(Class<T> otherType, CollisionListener<T> listener) {
        collisionListeners.put(otherType, listener);
    }

    @Override
    public <T> boolean shouldCollideWith(T otherObject) {
        Class<?> otherClass = otherObject.getClass();
        boolean shouldCollide = false;

        Iterator<Class<?>> iterator = collisionListeners.keySet().iterator();
        while (iterator.hasNext() && !shouldCollide) {
            Class<?> keyClass = iterator.next();
            shouldCollide = otherClass.isAssignableFrom(keyClass);
        }

        return shouldCollide;
    }

    @Override
    public <T> void triggerCollisionListeners(T otherObject, Body otherBody) {
        Class<?> otherClass = otherObject.getClass();

        for (Entry<Class<?>, CollisionListener<?>> entry : collisionListeners.entrySet()) {
            Class<?> key = entry.getKey();
            if (otherClass.isAssignableFrom(key)) {
                // Have to suppress this inspection because of type erasure;
                // Basically, because of the way setCollisionListener works, *we* know that the collision listener
                // will have the correct type, but Java doesn't know that.
                // FIXME I'm currently unsure of whether this will work with subclasses
                // noinspection unchecked
                CollisionListener<T> listener = (CollisionListener<T>) entry.getValue();
                listener.onCollision(otherObject);
            }
        }
    }
}
