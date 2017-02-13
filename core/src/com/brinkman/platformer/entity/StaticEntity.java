package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.brinkman.platformer.component.PhysicsComponent;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.physics.Body;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * @author Austin Brinkman.
 */
public class StaticEntity implements Entity
{
    private final ImmutableClassToInstanceMap<RootComponent> components;
	private final Rectangle bounds;

	public StaticEntity(float x, float y, float width, float height) {
        components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .put(PhysicsComponent.class, new PhysicsComponent())
                .build();

        Body body = components.getInstance(PhysicsComponent.class);
        assert body != null;
        body.getPosition().set(x, y);
	    body.setWidth(width);
	    body.setHeight(height);

		bounds = new Rectangle(x, y, width, height);

	}

	@Override
	public void render(float dt, Batch batch) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }

}
