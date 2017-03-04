package com.brinkman.platformer.component;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.render.RenderComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.input.ControllerProcessor;
import com.brinkman.platformer.input.KeyboardProcessor;
import com.brinkman.platformer.physics.ControlledBody;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static com.brinkman.platformer.util.Constants.CONTROLLER_PRESENT;

/**.
 * @author Austin Brinkman.
 */
public class InputOperator implements Operator
{
	private static final float HORIZONTAL_ACCELERATION = 0.8f;
	private final Collection<Class<? extends RootComponent>> requiredComponents;
	private final KeyboardProcessor keyboardProcessor;
	private ControllerProcessor controllerProcessor;

	/**
	 * Constructs InputOperator, used in correlation with InputComponent for handling input on Entities.
	 * @param entity Entity
	 */
	public InputOperator(Entity entity) {
		if (CONTROLLER_PRESENT) {
			controllerProcessor = new ControllerProcessor(entity);
		}
		keyboardProcessor = new KeyboardProcessor(entity);
		requiredComponents = new LinkedList<>();
		requiredComponents.add(PhysicsComponent.class);
		requiredComponents.add(RenderComponent.class);
	}

	/**
	 * Returns InputOperator's instance of ControllerProcessor.
	 * @return ControllerProcessor
	 */
	public ControllerProcessor getControllerProcessor() {
		return controllerProcessor;
	}

	/**
	 * Returns InputOperator's instance of KeyboardProcessor.
	 * @return KeyboardProcessor
	 */
	public KeyboardProcessor getKeyboardProcessor() {
		return keyboardProcessor;
	}

	@Override
	public Collection<Class<? extends RootComponent>> getRequiredComponents() {
		return Collections.unmodifiableCollection(requiredComponents);
	}

	@Override
	public void operate(float deltaT, Entity entity, GameWorld world) {
		InputComponent inputComponent = entity.getComponents().getInstance(InputComponent.class);
		PhysicsComponent physicsComponent = entity.getComponents().getInstance(PhysicsComponent.class);

		if((inputComponent != null) && (physicsComponent instanceof ControlledBody)) {
			boolean left = (controllerProcessor != null) ? controllerProcessor.left() : keyboardProcessor.left();
			boolean right = (controllerProcessor != null) ? controllerProcessor.right() : keyboardProcessor.right();
			boolean run = (controllerProcessor != null) ? controllerProcessor.run() : keyboardProcessor.run();
			ControlledBody body = (ControlledBody) physicsComponent;

			inputComponent.setKeyFlags(left, right, run);
			handleMovement(body, left, right, run);
		}
	}

	private static void handleMovement(ControlledBody body, boolean left, boolean right, boolean run) {
		//Run conditionals
		float moveSpeed = run ? 10 : 5;
		body.setMoveSpeed(moveSpeed);

		if(left) {
			body.getAcceleration().x = -HORIZONTAL_ACCELERATION;
		} else if (right) {
			body.getAcceleration().x = HORIZONTAL_ACCELERATION;
		}
	}
}
