package com.brinkman.platformer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.Platformer;
import static com.brinkman.platformer.util.Constants.*;

public class DesktopLauncher {
	private static Logger LOGGER = new Logger(DesktopLauncher.class.getName(), Logger.DEBUG);

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = APP_WIDTH;
		config.height = APP_HEIGHT;
		config.title = APP_TITLE;
	//	config.vSyncEnabled = V_SYNC;
		new LwjglApplication(new Platformer(), config);

		LOGGER.info("Launched");
	}
}
