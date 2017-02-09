package com.brinkman.platformer.entity.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.item.Item;
import com.brinkman.platformer.entity.actor.item.ItemType;
import com.brinkman.platformer.physics.Collidable;

/**
 * @author Austin Brinkman.
 */
public class Exit implements Entity
{
	private static final Logger LOGGER = new Logger(Exit.class.getName(), Logger.DEBUG);

	private final Rectangle bounds;
	private final GameWorld gameWorld;

	public Exit(GameWorld gameWorld, float x, float y, float width, float height) {
		this.gameWorld = gameWorld;
		bounds = new Rectangle(x, y, width, height);
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
}
