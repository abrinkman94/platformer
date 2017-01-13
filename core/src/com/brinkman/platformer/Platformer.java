package com.brinkman.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.brinkman.platformer.screen.GameScreen;
import com.brinkman.platformer.screen.LoadingScreen;
import com.brinkman.platformer.screen.PauseScreen;
import com.brinkman.platformer.screen.StartScreen;
import com.brinkman.platformer.util.AssetUtil;

public class Platformer extends Game {
	private GameScreen gameScreen;
	private PauseScreen pauseScreen;
	private StartScreen startScreen;
	private LoadingScreen loadingScreen;

	@Override
	public void create () {
		AssetUtil.loadAllAssets();

		loadingScreen = new LoadingScreen();
		gameScreen = new GameScreen();
		pauseScreen = new PauseScreen();
		startScreen = new StartScreen();

		setScreen(startScreen);
	}

	@Override
	public void render () {
		if (startScreen.isInitiateGameScreen()) {
			setScreen(gameScreen);
		}

		if (getScreen() == gameScreen) {
			if (!gameScreen.getMusic().isPlaying()) {
				gameScreen.playAudio();
			}
		}
		getScreen().render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {
		gameScreen.dispose();
		pauseScreen.dispose();
		startScreen.dispose();
	}
}
