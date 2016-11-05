package com.brinkman.platformer.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
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

	private static final float SAW_SPEED = 3.0f;
	private static final float SAW_WIDTH = 128;
	private static final float SAW_HEIGHT = 128;
	private static final int SAW_Y_OFFSET = 2;


	/**
	 * Constructs the Saw object.
	 * @param batch SpriteBatch
	 * @param x float x position
	 * @param y float y position
	 */
	public Saw(Batch batch, float x, float y) {
		this.batch = batch;
		position = new Vector2(x, y);
		width = SAW_WIDTH * TO_WORLD_UNITS;
		height = SAW_HEIGHT * TO_WORLD_UNITS;

		texture = new Texture("terrain/Object/saw.png");

		sprite = new Sprite(texture);
		sprite.setSize(width, height);
		sprite.setPosition(x, y + SAW_Y_OFFSET);
		sprite.setOrigin(width * 0.5f, height * 0.5f);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(position.x, position.y + SAW_Y_OFFSET, width, height);
	}

	@Override
	public void handleDeath() {

	}

	@Override
	public void render(float dt, Batch batch) {
		float rotationStep = sprite.getRotation() + SAW_SPEED;

		batch.begin();
		sprite.draw(batch);
		batch.end();

		sprite.setRotation(rotationStep);

	}

	@Override
	public void dispose() {
		texture.dispose();
	//	LOGGER.info("Disposed");
	}
}
