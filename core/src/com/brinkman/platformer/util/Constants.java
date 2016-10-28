package com.brinkman.platformer.util;

/**
 * Created by Austin on 9/29/2016.
 */
public final class Constants {

    private Constants() {}

    /**
     * float width of the application.
     */
    public static final int APP_WIDTH = 1080;
    /**
     * float height of the applicaiton.
     */
    public static final int APP_HEIGHT = 720;
    /**
     * String application title.
     */
    public static final String APP_TITLE = "Platformer";
    /**
     * float world units scale.
     */
    public static final float TO_WORLD_UNITS = 1/64f;
    /**
     * float world gravity.
     */
    public static final float GRAVITY = 0.426f;
    /**
     * float world max gravity.
     */
    public static final float MAX_GRAVITY = -15.0f;

    /**
     * boolean debug.
     */
    public static final boolean DEBUG = false;
}
