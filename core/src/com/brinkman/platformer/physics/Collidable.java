package com.brinkman.platformer.physics;

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

	/**
	 * Handles collision event logic.
	 * @param world GameWorld
	 */
	void handleCollisionEvent(GameWorld world);
}
