package com.brinkman.platformer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.entity.actor.*;
import com.brinkman.platformer.entity.actor.item.Item;
import com.brinkman.platformer.entity.actor.item.ItemType;
import com.brinkman.platformer.entity.actor.platform.Platform;
import com.brinkman.platformer.input.ControllerProcessor;
import com.brinkman.platformer.input.InputFlags;
import com.brinkman.platformer.input.KeyboardProcessor;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.map.TMXMap;
import com.brinkman.platformer.physics.Collidable;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.CameraUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class GameScreen implements Screen {
    private static final Logger LOGGER = new Logger(GameScreen.class.getName(), Logger.DEBUG);

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final Player player;
    private final HUD hud;
    private final GameWorld gameWorld;
    /**
     * Constructs the GameScreen.  GameScreen includes all of the render logic, basically the game loop.
     */
    public GameScreen() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(APP_WIDTH * TO_WORLD_UNITS, APP_HEIGHT * TO_WORLD_UNITS);
        gameWorld = new GameWorld(new Level(1, spriteBatch));
        InputFlags inputFlags = new InputFlags();
        player = new Player(inputFlags);
        hud = new HUD(gameWorld);

        gameWorld.addEntity(player);
        gameWorld.initializeMapObjects();

        if (CONTROLLER_PRESENT) {
            Controllers.addListener(new ControllerProcessor(inputFlags, player));
        } else {
            Gdx.input.setInputProcessor(new KeyboardProcessor(inputFlags, player));
        }

        LOGGER.info("Initialized");
    }

    private void placeholderKeyHandler() {
        if (gameWorld.getLevel().hasKey()) {
            float x = gameWorld.getLevel().getTmxMap().getMapObjects("exit").get(0).getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = gameWorld.getLevel().getTmxMap().getMapObjects("exit").get(0).getProperties().get("y", float.class) * TO_WORLD_UNITS;
            float width = gameWorld.getLevel().getTmxMap().getMapObjects("exit").get(0).getProperties().get("width", float.class) * TO_WORLD_UNITS;
            float height = gameWorld.getLevel().getTmxMap().getMapObjects("exit").get(0).getProperties().get("height", float.class) * TO_WORLD_UNITS;

            boolean render = false;
            for (Item item : player.getInventory()) {
                render = !(item.getItemType() == ItemType.KEY);
            }

            if (render) {
                ShapeRenderer renderer = new ShapeRenderer();
                renderer.setProjectionMatrix(camera.combined);
                renderer.begin(ShapeType.Filled);
                renderer.setColor(Color.GRAY);
                renderer.rect(x, y, width, height);
                renderer.end();
            }
        }
    }

    private void handleCollisions() {
        keepEntitiesInMap();

        Collection<Collidable> toBeRemoved = new LinkedList<>();
        for (Entity root : gameWorld.getEntities()) {
            for(Entity other : gameWorld.getEntities()) {
                if(root.shouldCollideWith(other) && root.intersects(other)) {
                    root.handleCollisionEvent(other);
                    if(root.shouldBeRemovedOnCollision()) {
                        toBeRemoved.add(root);
                    }
                }
            }
        }

        Entity exit = null;
        for (Entity entity : gameWorld.getEntities()) {
            if (entity instanceof Exit) {
                exit = entity;
            }
        }

        toBeRemoved.stream()
                   .filter(Entity.class::isInstance)
                   .map(Entity.class::cast)
                   .forEach(gameWorld::removeEntity);

        if(toBeRemoved.contains(player)) {
            // TODO Handle game over.
        } else {
            if (toBeRemoved.contains(exit)) {
				int levelNumber = gameWorld.getLevel().getLevelNumber();

				if (gameWorld.getLevel().getLevelNumber() < NUM_OF_LEVELS) {
					levelNumber++;
					gameWorld.setLevel(new Level(levelNumber, spriteBatch));

                    player.reset();
                    clearWorld();

					gameWorld.initializeMapObjects();
				} else {
					LOGGER.info("No more levels");
					Gdx.app.exit();
				}
			}
        }
    }

    private void clearWorld() {
        Collection<Entity> entitiesToRemove = gameWorld.getEntities()
              .stream()
              .filter(it -> !(it instanceof Player))
              .collect(Collectors.toList());
        entitiesToRemove.stream()
              .filter(Entity.class::isInstance)
              .map(Entity.class::cast)
              .forEach(gameWorld::removeEntity);
    }

    private void keepEntitiesInMap() {
        float mapLeft = 0;
        float mapRight = TMXMap.mapWidth;

        for (Entity entity : gameWorld.getEntities()) {
            if (!(entity instanceof StaticEntity) && !(entity instanceof Exit)) {
                Actor actor = (Actor) entity;

                Vector2 position = actor.getBody().getPosition();
                Vector2 velocity = actor.getBody().getVelocity();
                if (position.x <= mapLeft) {
                    position.x = mapLeft;
                    if (actor instanceof Enemy) {
                        velocity.x = -velocity.x;
                    }
                }
                if (position.x >= (mapRight - (actor.getBody().getWidth() * TO_WORLD_UNITS))) {
                    position.x = mapRight - (actor.getBody().getWidth() * TO_WORLD_UNITS);
                    if (actor instanceof Enemy) {
                        velocity.x = -velocity.x;
                    }
                }
            }
        }
    }

    @Override
    public void show() {
    }

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
        handleCollisions();

        //Placeholder for locked door
        placeholderKeyHandler();

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
