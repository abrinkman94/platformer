package com.brinkman.platformer.component.action;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.entity.Entity;

/**
 * @author Austin Brinkman.
 */
public interface ActionComponent extends RootComponent
{
	float getRemainingCooldown(ActionType actionType);

	void decrementCooldown(ActionType actionType, float amount);

	void setCooldown(ActionType actionType, float cooldown);

	void resetCooldowns(ActionType actionType);

	void doAction(ActionType actionType, Entity entity, GameWorld world);
}
