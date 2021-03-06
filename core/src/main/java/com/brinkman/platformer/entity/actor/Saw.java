package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.physics.StaticPhysicsComponent;
import com.brinkman.platformer.component.render.RenderComponent;
import com.brinkman.platformer.component.render.TextureRenderComponent;
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
		PhysicsComponent body = new StaticPhysicsComponent();

		body.getPosition().set(x, y);
		body.setWidth(SAW_WIDTH * TO_WORLD_UNITS);
		body.setHeight(SAW_HEIGHT * TO_WORLD_UNITS);

		texture = (Texture) AssetUtil.getAsset(TexturePaths.SAW_TEXTURE, Texture.class);

		TextureRegion sprite = new TextureRegion(texture);

		components = ImmutableClassToInstanceMap.<RootComponent>builder()
				.put(RenderComponent.class, new TextureRenderComponent(sprite))
				.put(PhysicsComponent.class, body)
				.build();
	}

	@Override
	public void dispose() { texture.dispose(); }

	@Override
	public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }
}
