package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.physics.Collidable;
import com.brinkman.platformer.util.Constants;

/**
 * @author Austin Brinkman.
 */
public class Enemy extends Actor
{
	private boolean isDead;

	public Enemy() {
		getBody().getPosition().set(4, 2);
		width = 32 * Constants.TO_WORLD_UNITS;
		height = 64 * Constants.TO_WORLD_UNITS;
		velocity = new Vector2(4 * Constants.TO_WORLD_UNITS, 0);

		texture = new Texture("sprites/idle/frame-1-right.png");
		sprite = new Sprite(texture);

		Vector2 position = getBody().getPosition();
		sprite.setPosition(position.x, position.y);
		sprite.setSize(width, height);
	}

	@Override
	public Shape2D getBounds() {
		Vector2 position = getBody().getPosition();
		return new Rectangle(position.x, position.y, width, height);
	}

	@Override
	public void handleCollisionEvent(Collidable other) {
		if(other instanceof Saw) {
			handleDeath();
		}
	}

	@Override
	public void render(float dt, Batch batch) {
		if (!isDead) {
			batch.begin();
			sprite.draw(batch);
			batch.end();

			Vector2 position = getBody().getPosition();
			position.x += velocity.x;
			sprite.setPosition(position.x, position.y);
		}
	}

	@Override
	public void dispose() {

	}
}
