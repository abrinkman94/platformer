package com.brinkman.platformer.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * @author Austin Brinkman.
 */
public final class AssetUtil
{
	public static final AssetManager ASSET_MANAGER = new AssetManager();

	private AssetUtil() {}

	public static void loadAllAssets() {
		//Idle frames
		for (int i = 1; i <= 2; i++) {
			loadAsset("sprites/Idle/frame-" + i + "-right.png", Texture.class);
			loadAsset("sprites/Idle/normal/frame-" + i + "-right.png", Texture.class);
		}

		//Jump frames
		for (int i = 1; i <= 2; i++) {
			loadAsset("sprites/Jump/frame-" + i + "-right.png", Texture.class);
			loadAsset("sprites/Jump/normal/frame-" + i + "-right.png", Texture.class);
		}

		//Running frames
		for (int i = 1; i <= 6; i++) {
			loadAsset("sprites/running/frame-" + i + "-right.png", Texture.class);
			loadAsset("sprites/running/normal/frame-" + i + "-right.png", Texture.class);
		}

		for (int i = 1; i <= 3; i++) {
			loadAsset("sprites/melee/frame-" + i + ".png", Texture.class);
			loadAsset("sprites/melee/normal/frame-" + i + ".png", Texture.class);
		}

		ASSET_MANAGER.setLoader(TiledMap.class, new TmxMapLoader(
				new InternalFileHandleResolver()));

		loadAsset("map/lighting-demo/TestLevel.tmx", TiledMap.class);

		finishLoading();
	}

	/**
	 * Loads asset, at assetPath, into the AssetManager.
	 * @param assetPath String path to asset
	 * @param assetClassType Class class of assetPath
	 */
	public static void loadAsset(String assetPath, Class assetClassType) {
		ASSET_MANAGER.load(assetPath, assetClassType);
	}

	/**
	 * Returns asset, at assetPath, from the AssetManager.
	 * @param assetPath String path to asset
	 * @param assetClass Class
	 *
	 * @return assetPath class
	 */
	public static Object getAsset(String assetPath, Class assetClass) {
		return ASSET_MANAGER.get(assetPath, assetClass);
	}

	/**
	 *Blocks processes until assets are finished loading.  Should be utilized to prevent null pointer exceptions when
	 * trying to access assets from AssetManager immediately after loading them into the AssetManager.
	 */
	public static void finishLoading() {
		ASSET_MANAGER.finishLoading();
	}

	/**
	 * Returns float loading progress of the AssetManager. Useful for 'progress bar'.
	 * @return float progress
	 */
	public static float getProgress() {
		return ASSET_MANAGER.getProgress();
	}

	/**
	 * Updates the AssetManager, keeping it loading any assets in the preload queue.
	 * @return true if all loading finished.
	 */
	public static boolean update() {
		return ASSET_MANAGER.update();
	}

	/**
	 * Disposes all assets in the manager and stops all asynchronous loading.
	 */
	public static void dispose() {
		ASSET_MANAGER.dispose();
	}
}
