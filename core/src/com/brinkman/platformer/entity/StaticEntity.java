package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.brinkman.platformer.physics.Collidable;

/**
 * @author Austin Brinkman.
 */
public class StaticEntity implements Entity
{
	private final Rectangle bounds;

	public StaticEntity(float x, float y, float width, float height) {
		bounds = new Rectangle(x, y, width, height);
	}

	@Override
	public void render(float dt, Batch batch) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public Shape2D getBounds() {
		return bounds;
	}

	@Override
	public void handleCollisionEvent(Collidable other) {

	}
}
