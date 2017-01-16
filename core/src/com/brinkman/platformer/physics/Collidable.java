package com.brinkman.platformer.physics;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.brinkman.platformer.GameWorld;

/**
 * @author Austin Brinkman.
 */
public interface Collidable
{
	/**
	 * Returns the Shape bounds of the collidable object.
	 * @return Shape
	 */
	Shape2D getBounds();

	void handleCollisionEvent(Collidable other);

	/**
	 * Determine whether this Collidable should collide with the one specified.
	 * @param other the other collidable.
	 * @return Whether the two should collide.
	 */
	default boolean shouldCollideWith(Collidable other) { return true; }

	default boolean shouldBeRemovedOnCollision() { return false; }

	default boolean intersects(Collidable other) {
		boolean intersects = false;
		if(getBounds() instanceof Circle) {
		    if(other.getBounds() instanceof Circle) {
		    	intersects =  Intersector.overlaps((Circle)getBounds(), (Circle)other.getBounds());
			} else if(other.getBounds() instanceof Rectangle) {
				intersects =  Intersector.overlaps((Circle)getBounds(), (Rectangle) other.getBounds());
			}
		} else if(getBounds() instanceof Rectangle) {
			if(other.getBounds() instanceof Circle) {
				intersects =  Intersector.overlaps((Circle) other.getBounds(), (Rectangle) getBounds());
			} else if(other.getBounds() instanceof Rectangle) {
				intersects =  Intersector.overlaps((Rectangle) getBounds(), (Rectangle) other.getBounds());
			}
		}
		return intersects;
	}
}
