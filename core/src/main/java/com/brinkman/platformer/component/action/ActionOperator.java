package com.brinkman.platformer.component.action;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.Operator;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.component.input.InputComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.status.StatusComponent;
import com.brinkman.platformer.entity.Entity;

import java.util.*;

import static com.brinkman.platformer.component.action.ActionType.*;

/**
 * @author Austin Brinkman.
 */
public class ActionOperator implements Operator
{
	private final Collection<Class<? extends RootComponent>> requiredComponents;

	public ActionOperator() {
		requiredComponents = new LinkedList<>();
		requiredComponents.add(ActionComponent.class);
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
		ActionComponent actionComponent = entity.getComponents().getInstance(ActionComponent.class);
		InputComponent inputComponent = entity.getComponents().getInstance(InputComponent.class);
		StatusComponent statusComponent = entity.getComponents().getInstance(StatusComponent.class);
		PhysicsComponent physicsComponent = entity.getComponents().getInstance(PhysicsComponent.class);

		assert (actionComponent != null) && (inputComponent != null);

		for (ActionType action : values()) {

			if (actionComponent.getRemainingCooldown(action) >= 0) {
				actionComponent.decrementCooldown(action, deltaT);
			}

			if(inputComponent.isMeleeActive() && (actionComponent.getRemainingCooldown(MELEE_ATTACK) <= 0)) {
			    actionComponent.doAction(MELEE_ATTACK, entity, world);
			}
		}
	}
}
