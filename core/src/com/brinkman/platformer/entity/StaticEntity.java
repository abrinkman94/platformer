package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.physics.Collidable;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * @author Austin Brinkman.
 */
public class StaticEntity implements Entity
{
    private final ImmutableClassToInstanceMap<RootComponent> components;
	private final Rectangle bounds;

	public StaticEntity(float x, float y, float width, float height) {
		bounds = new Rectangle(x, y, width, height);
		components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .build();
	}

	@Override
	public void render(float dt, Batch batch) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }

	@Override
	public Shape2D getBounds() {
		return bounds;
	}

	@Override
	public void handleCollisionEvent(Collidable other) {

	}
}
