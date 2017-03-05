package com.brinkman.platformer.component.action;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.Operator;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.component.input.InputComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.status.StatusComponent;
import com.brinkman.platformer.entity.Entity;

import java.util.*;

/**
 * @author Austin Brinkman.
 */
public class ActionOperator implements Operator
{
	private final Collection<Class<? extends RootComponent>> requiredComponents;

	public ActionOperator() {
		requiredComponents = new LinkedList<>();
		requiredComponents.add(InputComponent.class);
		requiredComponents.add(StatusComponent.class);
		requiredComponents.add(PhysicsComponent.class);

	}

	@Override
	public Collection<Class<? extends RootComponent>> getRequiredComponents() {
		return Collections.unmodifiableCollection(requiredComponents);
	}

	@Override
	public void operate(float deltaT, Entity entity, GameWorld world) {
		SimpleActionComponent actionComponent = entity.getComponents().getInstance(SimpleActionComponent.class);
		InputComponent inputComponent = entity.getComponents().getInstance(InputComponent.class);
		StatusComponent statusComponent = entity.getComponents().getInstance(StatusComponent.class);
		PhysicsComponent physicsComponent = entity.getComponents().getInstance(PhysicsComponent.class);

		assert actionComponent != null;

		for (ActionType action : ActionType.values()) {

			if (actionComponent.getRemainingCooldown(action) >= 0) {
				actionComponent.decrementCooldown(action, deltaT);
			}

			if (actionComponent.getRemainingCooldown(action) <= 0) {
				actionComponent.doAction(action, entity, world);
			}
		}
	}
}
