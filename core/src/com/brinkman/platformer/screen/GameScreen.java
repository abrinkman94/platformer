package com.brinkman.platformer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.*;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.physics.CollisionHandler;
import com.brinkman.platformer.util.CameraUtil;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class GameScreen implements Screen {
    private static final Logger LOGGER = new Logger(GameScreen.class.getName(), Logger.DEBUG);

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final Texture background;
    private final Player player;
    private final Array<Coin> coins;
    private final Array<Saw> saws;
    private final CollisionHandler collisionHandler;
    private final HUD hud;
    private Level level;
    private GameWorld gameWorld;

    /**
     * Constructs the GameScreen.  GameScreen includes all of the render logic, basically the game loop.
     */
    public GameScreen() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(APP_WIDTH * TO_WORLD_UNITS,
              APP_HEIGHT * TO_WORLD_UNITS);
        level = new Level(1, spriteBatch);
        gameWorld = new GameWorld(level);
        player = new Player(spriteBatch);
        background = new Texture("background.png");
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

        //Draws background
        spriteBatch.begin();
        for (int i = 0; i < ((APP_WIDTH * 2) * TO_WORLD_UNITS * 4); i+= APP_WIDTH * 2 * TO_WORLD_UNITS) {
            spriteBatch.draw(background, i, 0, (APP_WIDTH * 2) * TO_WORLD_UNITS, (APP_HEIGHT * 2) * TO_WORLD_UNITS);
        }
        spriteBatch.end();

        //Renders GameWorld (Entities, Level)
        gameWorld.render(camera, delta, spriteBatch);

        //Camera utility methods
        CameraUtil.centerCameraOnActor(player, camera);
        CameraUtil.keepCameraInMap(camera);

        //Collision checks
        collisionHandler.handleMapCollision(gameWorld);
        collisionHandler.handleSawCollision(saws, gameWorld);
        collisionHandler.handleCoinCollision(coins, gameWorld);
        collisionHandler.handleItemCollision(gameWorld);
//        collisionHandler.handleEnemyCollision(gameWorld);
        collisionHandler.handleExitCollision(gameWorld, coins, saws, spriteBatch);
        collisionHandler.keepActorInMap(player);

        //Debug
        if (DEBUG) {
            collisionHandler.debug(level.getMap(), player, camera, saws);
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
        player.dispose();
        spriteBatch.dispose();
        background.dispose();
        level.dispose();
        hud.dispose();

        for (Saw saw : saws) {
            saw.dispose();
        }

        for (Coin coin : coins) {
            coin.dispose();
        }

        LOGGER.info("Disposed");
    }
}
