package com.brinkman.platformer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.*;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.physics.CollisionHandler;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.CameraUtil;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class GameScreen implements Screen {
    private static final Logger LOGGER = new Logger(GameScreen.class.getName(), Logger.DEBUG);

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final Player player;
    private final Array<Coin> coins;
    private final Array<Saw> saws;
    private final CollisionHandler collisionHandler;
    private final HUD hud;
    private GameWorld gameWorld;

    /**
     * Constructs the GameScreen.  GameScreen includes all of the render logic, basically the game loop.
     */
    public GameScreen() {
        AssetUtil.loadAllAssets();

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(APP_WIDTH * TO_WORLD_UNITS, APP_HEIGHT * TO_WORLD_UNITS);
        gameWorld = new GameWorld(new Level(1, spriteBatch));
        player = new Player();
        collisionHandler = new CollisionHandler();
        coins = new Array<>();
        saws = new Array<>();
        hud = new HUD(coins, gameWorld);

        gameWorld.addEntity(player);
        gameWorld.initializeMapObjects(spriteBatch, coins, saws);

        LOGGER.info("Initialized");
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the SpriteBatch projection matrix
        spriteBatch.setProjectionMatrix(camera.combined);

        //Renders GameWorld (Entities, Level)
        gameWorld.render(camera, delta, spriteBatch);

        //Camera utility methods
        CameraUtil.lerpCameraToActor(player, camera);
        CameraUtil.handleZoom(gameWorld, camera);
        CameraUtil.keepCameraInMap(camera);

        //Collision checks
        collisionHandler.handleMapCollision(gameWorld);
        collisionHandler.handleSawCollision(saws, gameWorld);
        collisionHandler.handleCoinCollision(coins, gameWorld);
        collisionHandler.handleItemCollision(gameWorld);
        collisionHandler.handleExitCollision(gameWorld, coins, saws, spriteBatch);
        collisionHandler.keepActorInMap(player);

        //Placeholder for locked door
        if (gameWorld.getLevel().hasKey()) {
            float x = gameWorld.getLevel().getMap().getMapObjects("exit").get(0).getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = gameWorld.getLevel().getMap().getMapObjects("exit").get(0).getProperties().get("y", float.class) * TO_WORLD_UNITS;
            float width = gameWorld.getLevel().getMap().getMapObjects("exit").get(0).getProperties().get("width", float.class) * TO_WORLD_UNITS;
            float height = gameWorld.getLevel().getMap().getMapObjects("exit").get(0).getProperties().get("height", float.class) * TO_WORLD_UNITS;

            boolean render = !player.getItems().values().contains(ItemType.KEY);

            if (render) {
                ShapeRenderer renderer = new ShapeRenderer();
                renderer.setProjectionMatrix(camera.combined);
                renderer.begin(ShapeRenderer.ShapeType.Filled);
                renderer.setColor(Color.GRAY);
                renderer.rect(x, y, width, height);
                renderer.end();
            }
        }

        //Debug
        if (DEBUG) {
            collisionHandler.debug(gameWorld.getLevel().getMap(), player, camera, saws);
        }

        //Renders HUD
        hud.render(delta);

        //Updates camera
        camera.update();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        AssetUtil.dispose();
        spriteBatch.dispose();
        hud.dispose();
        gameWorld.dispose();

        LOGGER.info("Disposed");
    }
}
