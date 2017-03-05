package com.brinkman.platformer.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.physics.ControlledBody;
import com.brinkman.platformer.util.ControllerMappings;

/**
 * Created by Austin on 11/22/2016.
 */
public class ControllerProcessor extends AbstractInputMappings implements ControllerListener {
    private static final Logger LOGGER = new Logger(ControllerProcessor.class.getName(), Logger.DEBUG);

    private final Entity entity;

    public ControllerProcessor(Entity entity) {
        this.entity = entity;

        LOGGER.info(Controllers.getControllers().first().getName());
    }

    @Override
    public void connected(Controller controller) {
        LOGGER.info(controller.getName() + " CONNECTED");
    }

    @Override
    public void disconnected(Controller controller) {
        LOGGER.info(controller.getName() + " DISCONNECTED");
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (controller.getName().contains("Xbox")) {
            if (buttonCode == ControllerMappings.BUTTON_A) {
                ((ControlledBody)entity.getComponents().getInstance(PhysicsComponent.class)).setJumping(true);
            }
            if (buttonCode == ControllerMappings.BUTTON_B) {
                melee = true;
            }
        } else {
            if (buttonCode == ControllerMappings.PS4_BUTTON_X) {
                ((ControlledBody)entity.getComponents().getInstance(PhysicsComponent.class)).setJumping(true);
            }
            if (buttonCode == ControllerMappings.PS4_BUTTON_SQUARE) {
                melee = true;
            }
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if ((buttonCode == ControllerMappings.BUTTON_A) || (buttonCode == ControllerMappings.PS4_BUTTON_X)) {
            ((ControlledBody) entity.getComponents().getInstance(PhysicsComponent.class)).setJumping(false);
            ((ControlledBody) entity.getComponents().getInstance(PhysicsComponent.class)).setJustJumped(false);
        }
        if ((buttonCode == ControllerMappings.BUTTON_X) || (buttonCode == ControllerMappings.PS4_BUTTON_SQUARE)) {
            melee = false;
        }
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (controller.getName().contains("Xbox")) {
            if (axisCode == ControllerMappings.AXIS_LEFT_X) {
                left = value < -0.15f;
                right = value > 0.25f;
            }

            if (axisCode == ControllerMappings.AXIS_LEFT_TRIGGER) {
                run = (value >= 0.25f) || (value <= -0.25f);
            }
        } else {
            if (axisCode == ControllerMappings.PS4_AXIS_LEFT_X) {
                left = value < -0.15f;
                right = value > 0.25f;
            }

            if (axisCode == ControllerMappings.PS4_TRIGGER_L2) {
                run = (value >= 0.25f) || (value <= -0.25f);
            }
        }
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }
}
