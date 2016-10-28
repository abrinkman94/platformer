package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * @author Austin Brinkman.
 */
public class Saw extends Actor
{
	private static final Logger LOGGER = new Logger("Saw", Logger.DEBUG);

	private final Batch batch;


	/**
	 * Constructs the Saw object.
	 * @param batch SpriteBatch
	 * @param x float x position
	 * @param y float y position
	 */
	public Saw(Batch batch, float x, float y) {
		this.batch = batch;
		position = new Vector2(x, y);
		width = 96 * TO_WORLD_UNITS;
		height = 96 * TO_WORLD_UNITS;

		texture = new Texture("terrain/Object/saw.png");
		sprite = new Sprite(texture);

		sprite.setSize(width, height);
		sprite.setPosition(x, y + 2);
		sprite.setOrigin(width / 2, height / 2);
	}

	@Override
	public void render(float dt) {
		batch.begin();
		sprite.draw(batch);
		batch.end();
		sprite.setRotation(sprite.getRotation() + 1.5f);

	}

	@Override
	public void dispose() {
		texture.dispose();
		LOGGER.info("Disposed");
	}
}
