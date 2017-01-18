package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.physics.Collidable;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.TexturePaths;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * @author Austin Brinkman.
 */
public class Saw extends Actor {
	private static final Logger LOGGER = new Logger(Saw.class.getName(), Logger.DEBUG);

	private static final float SAW_SPEED = 100.0f;
	private static final float SAW_WIDTH = 128;
	private static final float SAW_HEIGHT = 128;
	private static final float SAW_Y_OFFSET = 0.5f;


	/**
	 * Constructs the Saw object.
	 * @param x float x position
	 * @param y float y position
	 */
	public Saw(float x, float y) {
		getBody().getPosition().set(x, y);
		getBody().setWidth(SAW_WIDTH * TO_WORLD_UNITS);
		getBody().setHeight(SAW_HEIGHT * TO_WORLD_UNITS);

		texture = (Texture) AssetUtil.getAsset(TexturePaths.SAW_TEXTURE, Texture.class);

		sprite = new Sprite(texture);
		sprite.setSize(getBody().getWidth(), getBody().getHeight());
		sprite.setPosition(x, y + SAW_Y_OFFSET);
		sprite.setOriginCenter();
	}

	@Override
	public Shape2D getBounds() {
		Vector2 position = getBody().getPosition();
		return new Circle(position.x + (getBody().getWidth() / 2), position.y + (0.5f * 3), getBody().getWidth() * 0.5f);
	}

	@Override
	public void handleCollisionEvent(Collidable other) {
	}

	@Override
	public boolean shouldCollideWith(Collidable other) { return false; }

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
