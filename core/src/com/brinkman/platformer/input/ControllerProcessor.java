package com.brinkman.platformer.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.util.ControllerMappings;

/**
 * Created by Austin on 11/22/2016.
 */
public class ControllerProcessor implements ControllerListener {
    private static final Logger LOGGER = new Logger(ControllerProcessor.class.getName(), Logger.DEBUG);

    private InputFlags inputFlags;
    private Player player;

    public ControllerProcessor(InputFlags inputFlags, Player player) {
        this.inputFlags = inputFlags;
        this.player = player;

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
        if (buttonCode == ControllerMappings.BUTTON_A) {
                inputFlags.setJump(true);
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        inputFlags.setJump(false);
        player.setJustJumped(false);
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (axisCode == ControllerMappings.AXIS_LEFT_X) {
            if (value < -0.15f) {
                inputFlags.setLeft(true);
            } else {
                inputFlags.setLeft(false);
            }
            if (value > 0.25f) {
                inputFlags.setRight(true);
            } else {
                inputFlags.setRight(false);
            }
        }

        if (axisCode == ControllerMappings.AXIS_LEFT_TRIGGER) {
            if (value >= 0.25f || value <= -0.25f) {
                inputFlags.setRun(true);
            } else {
                inputFlags.setRun(false);
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
