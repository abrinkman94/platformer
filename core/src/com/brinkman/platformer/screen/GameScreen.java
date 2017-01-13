package com.brinkman.platformer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.actor.*;
import com.brinkman.platformer.input.ControllerProcessor;
import com.brinkman.platformer.input.InputFlags;
import com.brinkman.platformer.input.KeyboardProcessor;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.physics.CollisionHandler;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.CameraUtil;

import java.util.Objects;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class GameScreen implements Screen {
    private static final Logger LOGGER = new Logger(GameScreen.class.getName(), Logger.DEBUG);

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final Player player;
    private final InputFlags inputFlags;
    private final Array<Coin> coins;
    private final Array<Saw> saws;
    private final CollisionHandler collisionHandler;
    private final HUD hud;
    private GameWorld gameWorld;
    private ParticleEffect pe;
    private ActorState tempState;
    private Music gameAudio;
    /**
     * Constructs the GameScreen.  GameScreen includes all of the render logic, basically the game loop.
     */
    public GameScreen() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(APP_WIDTH * TO_WORLD_UNITS, APP_HEIGHT * TO_WORLD_UNITS);
        gameWorld = new GameWorld(new Level(1, spriteBatch));
        inputFlags = new InputFlags();
        player = new Player(inputFlags);
        collisionHandler = new CollisionHandler();
        coins = new Array<>();
        saws = new Array<>();
        hud = new HUD(coins, gameWorld);
        gameAudio = (Music) AssetUtil.getAsset("audio/5-5-14(brinkybeats@gmail.com).mp3", Music.class);

        gameWorld.addEntity(player);
        gameWorld.initializeMapObjects(spriteBatch, coins, saws);

        if (CONTROLLER_PRESENT) {
            Controllers.addListener(new ControllerProcessor(inputFlags, player));
        } else {
            Gdx.input.setInputProcessor(new KeyboardProcessor(inputFlags, player));
        }

        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("particle/GROUND_LAND"),Gdx.files.internal("particle"));
        pe.getEmitters().first().getGravity().setHigh(GRAVITY, GRAVITY);
        pe.start();

        LOGGER.info("Initialized");
    }

    public Music getMusic() { return gameAudio; }

    public void playAudio() {
        gameAudio.setVolume(0.1f);
        gameAudio.play();
    }

    public void stopAudio() {
        gameAudio.stop();
    }

    private void placeholderKeyHandler() {
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
    }

    private void handleCollisions() {
        collisionHandler.handleMapCollision(gameWorld);
        collisionHandler.handleSawCollision(saws, gameWorld);
        collisionHandler.handleCoinCollision(coins, gameWorld);
        collisionHandler.handleItemCollision(gameWorld);
        collisionHandler.handleExitCollision(gameWorld, coins, saws, spriteBatch);
        collisionHandler.keepActorInMap(player);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        pe.update(delta);

        // Update the SpriteBatch projection matrix
        spriteBatch.setProjectionMatrix(camera.combined);

        //Renders GameWorld (Entities, Level)
        gameWorld.render(camera, delta, spriteBatch);

        //Camera utility methods
        CameraUtil.lerpCameraToActor(player, camera);
        CameraUtil.handleZoom(gameWorld, camera);
        CameraUtil.keepCameraInMap(camera);

        //Collision checks
        handleCollisions();

        //Placeholder for locked door
        placeholderKeyHandler();

        //Debug
        if (DEBUG) {
            collisionHandler.debug(gameWorld.getLevel().getMap(), player, camera, saws);
        }

        //Renders HUD
        hud.render(delta);

        //Updates camera
        camera.update();

        if (tempState == null) {
            if (player.getState() == ActorState.FALLING) {
                tempState = ActorState.FALLING;
            } else {
                tempState = null;
            }
        }

        //Temporary Particle Effect for landing
        if (tempState == ActorState.FALLING && (player.getState() == ActorState.IDLE || player.getState() == ActorState.GROUNDED)) {
            spriteBatch.begin();
            pe.getEmitters().first().setPosition(player.getPosition().x, player.getPosition().y);
            pe.draw(spriteBatch);
            spriteBatch.end();
            if (pe.isComplete())
                pe.reset();
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
        AssetUtil.dispose();
        spriteBatch.dispose();
        hud.dispose();
        gameWorld.dispose();
        gameAudio.dispose();

        LOGGER.info("Disposed");
    }
}
