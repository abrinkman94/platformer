package com.brinkman.platformer.component;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.render.AnimationType;
import com.brinkman.platformer.component.render.RenderComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.input.ControllerProcessor;
import com.brinkman.platformer.input.KeyboardProcessor;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.physics.ControlledBody;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static com.brinkman.platformer.component.render.AnimationType.*;
import static com.brinkman.platformer.component.render.AnimationType.IDLE_LEFT;
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
		RenderComponent renderComponent = entity.getComponents().getInstance(RenderComponent.class);

		if((inputComponent != null) && (physicsComponent instanceof ControlledBody) && (renderComponent != null)) {
			boolean left = (controllerProcessor != null) ? controllerProcessor.left() : keyboardProcessor.left();
			boolean right = (controllerProcessor != null) ? controllerProcessor.right() : keyboardProcessor.right();
			boolean run = (controllerProcessor != null) ? controllerProcessor.run() : keyboardProcessor.run();

			inputComponent.setLeftActive(left);
			inputComponent.setRightActive(right);
			inputComponent.setRunActive(run);

			ControlledBody body = (ControlledBody) physicsComponent;

			handleMovement(body, inputComponent);
			handleAnimationSwitching(renderComponent, body, inputComponent);
		}
	}

	private static void handleMovement(ControlledBody body, InputComponent input) {
		//Run conditionals
		float moveSpeed = input.isRunActive() ? 10 : 5;
		body.setMoveSpeed(moveSpeed);

		if(input.isLeftActive()) {
			body.getAcceleration().x = -HORIZONTAL_ACCELERATION;
		} else if (input.isRightActive()) {
			body.getAcceleration().x = HORIZONTAL_ACCELERATION;
		}
	}

	private void handleAnimationSwitching(RenderComponent renderComponent, ControlledBody body, InputComponent input) {
		AnimationType currentAnimation = null;

		if (input.isLeftActive()) {
			body.setFacingRight(false);
			if (input.isRunActive()) {
				currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_LEFT : RUN_LEFT;
			} else {
				currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_LEFT : WALK_LEFT;
			}
		} else if (input.isRightActive()) {
			body.setFacingRight(true);
			if (input.isRunActive()) {
				currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_RIGHT : RUN_RIGHT;
			} else {
				currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_RIGHT : WALK_RIGHT;
			}
		} else {
			float xSpeed = body.getVelocity().x;

			if (xSpeed == 0.0f) {
				if (body.isFacingRight()) {
					currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_RIGHT : IDLE_RIGHT;
				} else {
					currentAnimation = (body.isJumping() && !body.isGrounded()) ? JUMP_LEFT : IDLE_LEFT;
				}
			}
		}

		if(currentAnimation != null) {
			renderComponent.setAnimationType(currentAnimation);
		}
	}
}
