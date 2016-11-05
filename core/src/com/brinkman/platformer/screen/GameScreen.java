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
import com.badlogic.gdx.physics.box2d.World;
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
    private static final Logger LOGGER = new Logger("GameScreen", Logger.DEBUG);

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final Texture background;
    private final Player player;
    private final Enemy enemy;
    private final Array<Coin> coins;
    private final Array<Saw> saws;
    private final CollisionHandler collisionHandler;
    private final HUD hud;
    private Level level;
    private GameWorld gameWorld;

    private int levelNumber = 1;

    /**
     * Constructs the GameScreen.  GameScreen includes all of the render logic, basically the game loop.
     */
    public GameScreen() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(APP_WIDTH * TO_WORLD_UNITS,
              APP_HEIGHT * TO_WORLD_UNITS);
        background = new Texture("background.png");
        player = new Player(spriteBatch);
        enemy = new Enemy(spriteBatch);
        collisionHandler = new CollisionHandler();
        coins = new Array<>();
        saws = new Array<>();
        hud = new HUD(coins, player);
        level = new Level(1, spriteBatch);
        gameWorld = new GameWorld(level);

        gameWorld.addEntity(player);
        gameWorld.addEntity(enemy);

        for (MapObject object : gameWorld.getLevel().getMap().getMapObjects("coins")) {
            float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;

            final float coinYOffset = 1.0f;
            Coin coin = new Coin(spriteBatch, x, y + coinYOffset);
            coins.add(coin);
            gameWorld.addEntity(coin);
        }

        if (gameWorld.getLevel().getMap().getMapObjects("saw") != null) {
            for (MapObject sawObject : gameWorld.getLevel().getMap().getMapObjects("saw")) {
                float x = sawObject.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = sawObject.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Saw saw = new Saw(spriteBatch, x, y);
                saws.add(saw);
                gameWorld.addEntity(saw);
            }
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

        gameWorld.render(camera, delta, spriteBatch);

        //Camera utility methods
        CameraUtil.centerCameraOnActor(player, camera);
        CameraUtil.keepCameraInMap(camera);

        //Collision checks
        collisionHandler.handleMapCollision(gameWorld);
        collisionHandler.handleSawCollision(saws, gameWorld);
        collisionHandler.handleCoinCollision(coins, gameWorld);
        collisionHandler.handleEnemyCollision(level, gameWorld);
        collisionHandler.keepActorInMap(player);
        collisionHandler.keepActorInMap(enemy);

        //Debug
        if (DEBUG) {
            collisionHandler.debug(level.getMap(), player, camera, saws);
        }

        //Renders HUD
        hud.render(delta);

        //Checks for exit(end of level) conditions
        if (canEndLevel()) {
            endLevel();
        }

        //Updates camera
        camera.update();
    }

    private boolean canEndLevel() {
        boolean canEnd = false;

        if (coins.size <= 0) {
            Rectangle playerBounds = gameWorld.getEntity("player").getBounds();

            MapObjects mapObjects = gameWorld.getLevel().getMap().getMapObjects("exit");

            for (MapObject object : mapObjects) {
                float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
                float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
                float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

                Rectangle exitBounds = new Rectangle(x, y, width, height);

                 canEnd = playerBounds.overlaps(exitBounds);
            }
        }
        return canEnd;
    }

    private void endLevel() {
        if (level.getLevelNumber() < 3) {
            levelNumber++;
            gameWorld.setLevel(new Level(levelNumber, spriteBatch));
            player.reset();

            coins.clear();
            for (MapObject mapCoin : gameWorld.getLevel().getMap().getMapObjects("coins")) {
                float coinX = mapCoin.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float coinY = mapCoin.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Coin coin = new Coin(spriteBatch, coinX, coinY + 1);
                coins.add(coin);
                gameWorld.addEntity(coin);
            }

            saws.clear();
            for (MapObject mapSaw : gameWorld.getLevel().getMap().getMapObjects("saw")) {
                float sawX = mapSaw.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float sawY = mapSaw.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                Saw saw = new Saw(spriteBatch, sawX, sawY);
                saws.add(saw);
                gameWorld.addEntity(saw);
            }
        } else {
            Gdx.app.exit();
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

        for (Saw saw : saws) {
            saw.dispose();
        }

        for (Coin coin : coins) {
            coin.dispose();
        }

        LOGGER.info("Disposed");
    }
}
