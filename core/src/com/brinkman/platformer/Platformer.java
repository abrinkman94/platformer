package com.brinkman.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.brinkman.platformer.screen.GameScreen;
import com.brinkman.platformer.screen.PauseScreen;
import com.brinkman.platformer.screen.StartScreen;

public class Platformer extends Game {
	private GameScreen gameScreen;
	private PauseScreen pauseScreen;
	private StartScreen startScreen;

	@Override
	public void create () {
		gameScreen = new GameScreen();
		pauseScreen = new PauseScreen();
		startScreen = new StartScreen();

		setScreen(gameScreen);
	}

	@Override
	public void render () {
		getScreen().render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {
		gameScreen.dispose();
		pauseScreen.dispose();
	}
}
