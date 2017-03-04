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
import com.brinkman.platformer.component.input.InputOperator;
import com.brinkman.platformer.component.Operator;
import com.brinkman.platformer.component.render.RenderOperator;
import com.brinkman.platformer.component.physics.ControlledPhysicsComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.physics.PhysicsOperator;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.entity.actor.item.Item;
import com.brinkman.platformer.entity.actor.item.ItemType;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.map.TMXMap;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.CameraUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class GameScreen implements Screen {
    private static final Logger LOGGER = new Logger(GameScreen.class.getName(), Logger.DEBUG);

    private final Operator physicsSubsystem;
    private final Operator renderSubsystem;
    private final Operator inputSubsystem;
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
        gameWorld = new GameWorld(new Level(1));
        player = new Player();
        hud = new HUD(gameWorld);

        gameWorld.addEntity(player);
        gameWorld.initializeMapObjects();

        physicsSubsystem = new PhysicsOperator(player);
        renderSubsystem = new RenderOperator(spriteBatch);
        inputSubsystem = new InputOperator(player);

        if (CONTROLLER_PRESENT) {
            Controllers.addListener(((InputOperator)inputSubsystem).getControllerProcessor());
        } else {
            Gdx.input.setInputProcessor(((InputOperator)inputSubsystem).getKeyboardProcessor());
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
            Body body = entity.getComponents().getInstance(PhysicsComponent.class);
            assert body != null;

            Vector2 position = body.getPosition();
            if (position.x <= mapLeft) {
                position.x = mapLeft;
            }
            if (position.x >= (mapRight - (body.getWidth() * TO_WORLD_UNITS))) {
                position.x = mapRight - (body.getWidth() * TO_WORLD_UNITS);
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

        // TODO Prooooobably should do this more efficiently
        Collection<Entity> entitiesCopy = new ArrayList<>(gameWorld.getEntities());

        //Renders GameWorld background
        gameWorld.renderBackground(camera, delta, spriteBatch);

        // Do physics, yo.
        entitiesCopy.stream()
                    .filter(it -> it.getComponents().keySet().containsAll(physicsSubsystem.getRequiredComponents()))
                    .forEach(it -> physicsSubsystem.operate(delta, it, gameWorld));
        // Render Entities
        entitiesCopy.stream()
                    .filter(it -> it.getComponents().keySet().containsAll(renderSubsystem.getRequiredComponents()))
                    .forEach(it -> renderSubsystem.operate(delta, it, gameWorld));
        // Do input, dayglo
        entitiesCopy.stream()
                    .filter(it -> it.getComponents().keySet().containsAll(inputSubsystem.getRequiredComponents()) &&
                          (it.getComponents().getInstance(PhysicsComponent.class) instanceof ControlledPhysicsComponent))
                    .forEach(it -> inputSubsystem.operate(delta, it, gameWorld));

        // Render gameworld foreground
        gameWorld.renderForeground(camera, spriteBatch);

        //Camera utility methods
        CameraUtil.lerpCameraToActor(player, camera);
        CameraUtil.handleZoom(gameWorld, camera);
        CameraUtil.keepCameraInMap(camera);

        //Collision checks
        keepEntitiesInMap();

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
