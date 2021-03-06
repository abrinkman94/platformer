package com.brinkman.platformer.util;

import com.badlogic.gdx.controllers.PovDirection;

/**
 * Created by Austin on 10/26/2016.
 */
public final class ControllerMappings {

    private ControllerMappings() {}

    //Xbox Controller Mappings
    public static final int BUTTON_X = 2;
    public static final int BUTTON_Y = 3;
    public static final int BUTTON_A = 0;
    public static final int BUTTON_B = 1;
    public static final int BUTTON_BACK = 6;
    public static final int BUTTON_START = 7;
    public static final PovDirection BUTTON_DPAD_UP = PovDirection.north;
    public static final PovDirection BUTTON_DPAD_DOWN = PovDirection.south;
    public static final PovDirection BUTTON_DPAD_RIGHT = PovDirection.east;
    public static final PovDirection BUTTON_DPAD_LEFT = PovDirection.west;
    public static final int BUTTON_LB = 4;
    public static final int BUTTON_L3 = 8;
    public static final int BUTTON_RB = 5;
    public static final int BUTTON_R3 = 9;
    public static final int AXIS_LEFT_X = 1; //-1 is left | +1 is right
    public static final int AXIS_LEFT_Y = 0; //-1 is up | +1 is down
    public static final int AXIS_LEFT_TRIGGER = 4; //value 0 to 1f
    public static final int AXIS_RIGHT_X = 3; //-1 is left | +1 is right
    public static final int AXIS_RIGHT_Y = 2; //-1 is up | +1 is down
    public static final int AXIS_RIGHT_TRIGGER = 4; //value 0 to -1f

    //Playstation Controller Mappings
    public static final int PS4_BUTTON_X = 1;
    public static final int PS4_BUTTON_CIRCLE = 2;
    public static final int PS4_BUTTON_SQUARE = 0;
    public static final int PS4_BUTTON_TRIANGLE = 3;
    public static final int PS4_BUTTON_START = 9;
    public static final int PS4_BUTTON_GUIDE = 12;
    public static final int PS4_BUTTON_BACK = 13;
    public static final int PS4_BUTTON_L3 = 10;
    public static final int PS4_BUTTON_R3 = 11;
    public static final int PS4_BUTTON_L1 = 4;
    public static final int PS4_BUTTON_R1 = 5;
    public static final double PS4_BUTTON_DPUP = 0.1;
    public static final double PS4_BUTTON_DPLEFT = 0.8;
    public static final double PS4_BUTTON_DPDOWN = 0.4;
    public static final double PS4_BUTTON_DPRIGHT = 0.2;
    public static final int PS4_AXIS_LEFT_X = 0;
    public static final int PS4_AXIS_LEFT_Y = 1;
    public static final int PS4_AXIS_RIGHT_X = 2;
    public static final int PS4_AXIS_RIGHT_Y = 5;
    public static final int PS4_TRIGGER_L2 = 4;
    public static final int PS4_TRIGGER_R2 = 4;
}
