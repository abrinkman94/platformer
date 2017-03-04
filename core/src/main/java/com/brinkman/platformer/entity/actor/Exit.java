package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.physics.ControlledPhysicsComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.component.physics.StaticPhysicsComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.item.Item;
import com.brinkman.platformer.entity.actor.item.ItemType;
import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * @author Austin Brinkman.
 */
public class Exit implements Entity
{
	private static final Logger LOGGER = new Logger(Exit.class.getName(), Logger.DEBUG);

	private final GameWorld gameWorld;

	private final ImmutableClassToInstanceMap<RootComponent> components;

	public Exit(GameWorld gameWorld, float x, float y, float width, float height) {
		this.gameWorld = gameWorld;

		// HACK Currently overriding shouldCollideWith in body; there's probably a better solution.
		StaticPhysicsComponent body = new StaticPhysicsComponent() {
			@Override
			public <T> boolean shouldCollideWith(T otherObject) {
				if (otherObject instanceof Player) {
					Player player = (Player) otherObject;
					if (isLevelComplete(player)) {
						return true;
					}
				}
				return false;
			}
		};
		body.setRemovedOnCollision(true);
		body.getPosition().set(x, y);
		body.setWidth(width);
		body.setHeight(height);

		components = ImmutableClassToInstanceMap.<RootComponent>builder()
				.put(PhysicsComponent.class, body)
				.build();
	}

	private boolean isLevelComplete(Player player) {
		if (gameWorld.getNumberOfCoins() <= 0) {
			boolean playerHasKey = false;
			for (Item item : player.getInventory()) {
				playerHasKey = item.getItemType() == ItemType.KEY;
			}

			if (!gameWorld.getLevel().hasKey() || playerHasKey) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void dispose() {

	}


    @Override
	public ImmutableClassToInstanceMap<RootComponent> getComponents() { return components; }

}
