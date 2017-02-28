package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.component.physics.ControlledPhysicsComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.RenderComponent;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.component.physics.StaticPhysicsComponent;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.TexturePaths;
import com.google.common.collect.ImmutableClassToInstanceMap;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * @author Austin Brinkman.
 */
public class Saw extends Actor {
	private static final Logger LOGGER = new Logger(Saw.class.getName(), Logger.DEBUG);

	private static final float SAW_SPEED = 100.0f;
	private static final float SAW_WIDTH = 128;
	private static final float SAW_HEIGHT = 128;

	private final ImmutableClassToInstanceMap<RootComponent> components;

	/**
	 * Constructs the Saw object.
	 * @param x float x position
	 * @param y float y position
	 */
	public Saw(float x, float y) {
		components = ImmutableClassToInstanceMap.<RootComponent>builder()
				.put(RenderComponent.class, this::render)
                .put(PhysicsComponent.class, new StaticPhysicsComponent())
				.build();

		Body body = components.getInstance(PhysicsComponent.class);
		assert body != null;

		body.getPosition().set(x, y);
		body.setWidth(SAW_WIDTH * TO_WORLD_UNITS);
		body.setHeight(SAW_HEIGHT * TO_WORLD_UNITS);

		texture = (Texture) AssetUtil.getAsset(TexturePaths.SAW_TEXTURE, Texture.class);

		float width = body.getWidth();
		float height = body.getHeight();
		sprite = new Sprite(texture);
		sprite.setSize(width, height);
		sprite.setPosition(x, y);
		sprite.setOriginCenter();
	}

	private void render(float dt, Batch batch, Body body) {
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

	@Override
	public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }
}
