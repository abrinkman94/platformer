package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.util.Constants;

/**
 * @author Austin Brinkman.
 */
public class Enemy extends Actor
{
	private SpriteBatch batch;

	private boolean isDead;

	public Enemy(SpriteBatch batch) {
		this.batch = batch;
		position = new Vector2(4, originPosition.y);
		width = 32 * Constants.TO_WORLD_UNITS;
		height = 64 * Constants.TO_WORLD_UNITS;
		velocity = new Vector2(4 * Constants.TO_WORLD_UNITS, 0);

		texture = new Texture("sprites/idle/frame-1-right.png");
		sprite = new Sprite(texture);

		sprite.setPosition(position.x, position.y);
		sprite.setSize(width, height);
	}

	@Override
	public void render(float dt) {
		if (!isDead) {
			batch.begin();
			sprite.draw(batch);
			batch.end();

			position.x += velocity.x;
			sprite.setPosition(position.x, position.y);
		}
	}

	@Override
	public void dispose() {

	}
}
