package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.item.Item;
import com.brinkman.platformer.entity.actor.item.ItemType;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.physics.Collidable;
import com.brinkman.platformer.physics.PhysicsBody;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * @author Austin Brinkman.
 */
public class Exit implements Entity
{
	private static final Logger LOGGER = new Logger(Exit.class.getName(), Logger.DEBUG);

	private final Body body;
	private final Rectangle bounds;
	private final GameWorld gameWorld;

	private final ImmutableClassToInstanceMap<RootComponent> components;

	public Exit(GameWorld gameWorld, float x, float y, float width, float height) {
		this.gameWorld = gameWorld;

		body = new PhysicsBody();
		body.getPosition().set(x, y);
		body.setWidth(width);
		body.setHeight(height);

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
	public boolean shouldBeRemovedOnCollision() { return true; }

	@Override
	public boolean shouldCollideWith(Collidable other) {
		if (other instanceof Player) {
			if (gameWorld.getNumberOfCoins() <= 0) {
				boolean playerHasKey = false;
				for (Item item : ((Player)other).getInventory()) {
					playerHasKey = item.getItemType() == ItemType.KEY;
				}

				if (!gameWorld.getLevel().hasKey() || playerHasKey) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Shape2D getBounds() {
		return bounds;
	}

	@Override
	public void handleCollisionEvent(Collidable other) {

	}

	@Override
	public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }

	@Override
	public Body getBody() { return body; }
}
