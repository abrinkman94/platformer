package com.brinkman.platformer.util;

import com.badlogic.gdx.assets.AssetManager;

/**
 * @author Austin Brinkman.
 */
public final class AssetUtil
{
	private static final AssetManager ASSET_MANAGER = new AssetManager();

	private AssetUtil() {}

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
	 *
	 * @return assetPath class
	 */
	public static Class getAsset(String assetPath) {
		return ASSET_MANAGER.get(assetPath);
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
