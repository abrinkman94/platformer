package com.brinkman.platformer.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
		for (int i = 1; i <= 2; i++) {
			loadAsset("sprites/Idle/frame-" + i + "-left.png", Texture.class);
			loadAsset("sprites/Idle/frame-" + i + "-right.png", Texture.class);
		}

		for (int i = 1; i <= 2; i++) {
			loadAsset("sprites/Jump/frame-" + i + "-left.png", Texture.class);
			loadAsset("sprites/Jump/frame-" + i + "-right.png", Texture.class);
		}

		for (int i = 1; i <= 6; i++) {
			loadAsset("sprites/running/frame-" + i + "-left.png", Texture.class);
			loadAsset("sprites/running/frame-" + i + "-right.png", Texture.class);
		}

		loadAsset("sprites/coinsheet.png", Texture.class);
		loadAsset("map/Object/saw.png", Texture.class);
		loadAsset("map/Object/life.png", Texture.class);
		loadAsset("map/Object/key.png", Texture.class);
		loadAsset("map/Object/HUD key.png", Texture.class);

		ASSET_MANAGER.setLoader(TiledMap.class, new TmxMapLoader(
				new InternalFileHandleResolver()));
		loadAsset("map/level1.tmx", TiledMap.class);
		loadAsset("map/level2.tmx", TiledMap.class);
		loadAsset("map/level3.tmx", TiledMap.class);
		loadAsset("map/level4.tmx", TiledMap.class);

		for (int i = 1; i <= Constants.NUM_OF_LEVELS; i++) {
			loadAsset("map/World 1/level" + i + ".tmx", TiledMap.class);
		}

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
