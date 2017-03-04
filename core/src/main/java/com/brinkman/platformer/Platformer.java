package com.brinkman.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.brinkman.platformer.screen.GameScreen;
import com.brinkman.platformer.util.AssetUtil;

public class Platformer extends Game {
	private GameScreen gameScreen;

	@Override
	public void create () {
		AssetUtil.loadAllAssets();

		gameScreen = new GameScreen();
		setScreen(gameScreen);
	}

	@Override
	public void render () {
		getScreen().render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {
		gameScreen.dispose();
	}
}
