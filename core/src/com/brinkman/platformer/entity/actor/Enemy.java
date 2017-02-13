package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.component.PhysicsComponent;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.util.Constants;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * @author Austin Brinkman.
 */
public class Enemy extends Actor
{
	private final ImmutableClassToInstanceMap<RootComponent> components;
	private boolean isDead;

	public Enemy() {
		components = ImmutableClassToInstanceMap.<RootComponent>builder()
                .put(PhysicsComponent.class, new PhysicsComponent())
				.build();

		Body body = components.getInstance(PhysicsComponent.class);

		assert body != null;
		body.getPosition().set(4, 2);
		body.setWidth(32 * Constants.TO_WORLD_UNITS);
		body.setHeight(64 * Constants.TO_WORLD_UNITS);
		body.getVelocity().set(4 * Constants.TO_WORLD_UNITS, 0);
		body.setCollisionListener(Saw.class, this::handleSawCollision);

		texture = new Texture("sprites/idle/frame-1-right.png");
		sprite = new Sprite(texture);

		Vector2 position = body.getPosition();
		float width = body.getWidth();
		float height = body.getHeight();
		sprite.setPosition(position.x, position.y);
		sprite.setSize(width, height);
	}

	private void handleSawCollision(Saw saw) { handleDeath(); }

	@Override
	public void render(float dt, Batch batch) {
		if (!isDead) {
			batch.begin();
			sprite.draw(batch);
			batch.end();

			Body body = components.getInstance(PhysicsComponent.class);

			assert body != null;
			Vector2 position = body.getPosition();
			Vector2 velocity = body.getVelocity();
			position.x += velocity.x;
			sprite.setPosition(position.x, position.y);
		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }
}
