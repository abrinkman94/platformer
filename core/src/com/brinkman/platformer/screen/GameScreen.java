package com.brinkman.platformer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.entity.Coin;
import com.brinkman.platformer.entity.Player;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.physics.CollisionHandler;
import com.brinkman.platformer.util.CameraUtil;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class GameScreen implements Screen {
    private static final Logger LOGGER = new Logger("GameScreen", Logger.DEBUG);

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final Texture background;
    private final Player player;
    private final Array<Coin> coins;
    private final CollisionHandler collisionHandler;
    private final HUD hud;
    private Level level;

    public GameScreen() {
        spriteBatch = new SpriteBatch();
    //    map = new TMXMap(spriteBatch, "terrain/map.tmx");
        camera = new OrthographicCamera(APP_WIDTH * TO_WORLD_UNITS,
              APP_HEIGHT * TO_WORLD_UNITS);
        background = new Texture("background.png");
        player = new Player(spriteBatch);
        collisionHandler = new CollisionHandler();
        coins = new Array<>();
        hud = new HUD(camera, coins, player);
        level = new Level(1, spriteBatch);

        for (MapObject object : level.getMap().getMapObjects("coins")) {
            float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;

            Coin coin = new Coin(spriteBatch, x, y + 1);
            coins.add(coin);
        }

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

        //Renders the level and it's associated TMXMap
        level.render(camera);

        //Renders coins
        for (Coin coin : coins) {
            coin.render(delta);
        }

        //Renders player
        player.render(delta);

        //Camera utility methods
        CameraUtil.centerCameraOnActor(player, camera);
        CameraUtil.keepCameraInMap(camera);

        //Collision checks
        collisionHandler.handleMapCollision(player, level.getMap());
        collisionHandler.handleWaterCollision(player, level.getMap());
        collisionHandler.handleCoinCollision(player, coins);
    //    collisionHandler.handleExitCollision(level, player, coins, spriteBatch);
        collisionHandler.keepPlayerInMap(player);

        //Debug
        if (DEBUG) {
            collisionHandler.debug(level.getMap(), player, camera);
        }

        //Renders HUD
        hud.render(delta);

        //Checks for exit(end of level) conditions
        endLevel();

        //Updates camera
        camera.update();
    }

    private void endLevel() {
        if (coins.size <= 0) {
            Rectangle playerBounds = player.getBounds();

            MapObjects mapObjects = level.getMap().getMapObjects("exit");

            for (MapObject object : mapObjects) {
                float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
                float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
                float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

                Rectangle exitBounds = new Rectangle(x, y, width, height + 1);

                if (playerBounds.overlaps(exitBounds)) {
                    if (level.getLevelNumber() < 2) {
                        level = new Level(2, spriteBatch);
                        player.reset();

                        for (MapObject mapCoin : level.getMap().getMapObjects("coins")) {
                            float coinX = mapCoin.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                            float coinY = mapCoin.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                            Coin coin = new Coin(spriteBatch, coinX, coinY + 1);
                            coins.add(coin);
                        }
                    } else {
                        Gdx.app.exit();
                    }
                }
            }
        }
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

        LOGGER.info("Disposed");
    }
}
