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
		loadAsset("sprites/Idle/frame-1-left.png", Texture.class);
		loadAsset("sprites/Idle/frame-1-right.png", Texture.class);
		loadAsset("sprites/Idle/frame-2-left.png", Texture.class);
		loadAsset("sprites/Idle/frame-2-right.png", Texture.class);
		loadAsset("sprites/Jump/frame-1-left.png", Texture.class);
		loadAsset("sprites/Jump/frame-1-right.png", Texture.class);
		loadAsset("sprites/Jump/frame-2-left.png", Texture.class);
		loadAsset("sprites/Jump/frame-2-right.png", Texture.class);
		loadAsset("sprites/running/frame-1-left.png", Texture.class);
		loadAsset("sprites/running/frame-1-right.png", Texture.class);
		loadAsset("sprites/running/frame-2-left.png", Texture.class);
		loadAsset("sprites/running/frame-2-right.png", Texture.class);
		loadAsset("sprites/running/frame-3-left.png", Texture.class);
		loadAsset("sprites/running/frame-3-right.png", Texture.class);
		loadAsset("sprites/running/frame-4-left.png", Texture.class);
		loadAsset("sprites/running/frame-4-right.png", Texture.class);
		loadAsset("sprites/running/frame-5-left.png", Texture.class);
		loadAsset("sprites/running/frame-5-right.png", Texture.class);
		loadAsset("sprites/running/frame-6-left.png", Texture.class);
		loadAsset("sprites/running/frame-6-right.png", Texture.class);
		loadAsset("sprites/coinsheet.png", Texture.class);
		loadAsset("terrain/Object/saw.png", Texture.class);
		loadAsset("terrain/Object/life.png", Texture.class);
		loadAsset("terrain/Object/key.png", Texture.class);
		loadAsset("terrain/Object/HUD key.png", Texture.class);
		ASSET_MANAGER.setLoader(TiledMap.class, new TmxMapLoader(
				new InternalFileHandleResolver()));
		loadAsset("terrain/level1.tmx", TiledMap.class);
		loadAsset("terrain/level2.tmx", TiledMap.class);
		loadAsset("terrain/level3.tmx", TiledMap.class);
		loadAsset("terrain/level4.tmx", TiledMap.class);

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
