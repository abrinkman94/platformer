package com.brinkman.platformer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
import com.brinkman.platformer.util.Shaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 9/29/2016.
 */
public class GameScreen implements Screen {
    private static final Logger LOGGER = new Logger(GameScreen.class.getName(), Logger.DEBUG);
    private static final String RESOLUTION_UNIFORM = "screenResolution";
    private static final String AMBIENT_UNIFORM = "ambientColor";
    private static final String POINT_COLOR_UNIFORM = "pointLightColor";
    private static final String POINT_POSITION_UNIFORM = "pointLightPosition";
    private static final String POINT_FALLOFF_UNIFORM = "pointLightFalloff";
    private static final Vector3 AMBIENT_COLOR = new Vector3(0.0f, 0.0f, 0.0f);
    private static final Vector3 POINT_COLOR = new Vector3(1.0f, 1.0f,1.0f);
    private static final Vector3 POINT_FALLOFF = new Vector3(1.0f, 0.0f, 0.0f);
    private static final float AMBIENT_INTENSITY = 1.0f;
    private static final float POINT_INTENSITY = 1.0f;

    private final Operator physicsSubsystem;
    private final Operator renderSubsystem;
    private final Operator inputSubsystem;
    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final OrthographicCamera bufferCamera;
    private final Player player;
    private final HUD hud;
    private final GameWorld gameWorld;
    private final ShaderProgram shader;
    private final ShaderProgram defaultShader;

    private FrameBuffer colorBuffer;
    private FrameBuffer normalBuffer;

    /**
     * Constructs the GameScreen.  GameScreen includes all of the render logic, basically the game loop.
     */
    public GameScreen() {
        spriteBatch = new SpriteBatch();
        gameWorld = new GameWorld(new Level("map/lighting-demo/cave.tmx"));
        float viewportWidth = APP_WIDTH * TO_WORLD_UNITS;
        float viewportHeight = APP_HEIGHT * TO_WORLD_UNITS;
        camera = new OrthographicCamera(APP_WIDTH, APP_HEIGHT);
        bufferCamera = new OrthographicCamera(viewportWidth, viewportHeight);
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

        defaultShader = SpriteBatch.createDefaultShader();

        ShaderProgram lightingShaderTemp;
        try {
            lightingShaderTemp = Shaders.load();
        } catch (IOException e) {
            LOGGER.info("Unable to load lighting shader", e);
            lightingShaderTemp = defaultShader;
        }

        shader = lightingShaderTemp;
        shader.begin();
        shader.setUniformi("u_normals", 1);
        shader.end();

        LOGGER.info("Initialized");
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
        drawColorBuffer(delta);
        Texture colorTexture = colorBuffer.getColorBufferTexture();

        drawNormalBuffer(delta);
        Texture normalTexture = normalBuffer.getColorBufferTexture();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set shader uniforms
        setShaderUniforms();

        spriteBatch.setShader(shader);

        // Update the SpriteBatch projection matrix
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        normalTexture.bind(1);
        colorTexture.bind(0);
        spriteBatch.draw(colorTexture, 0, 0);
        spriteBatch.end();

        // TODO Prooooobably should do this more efficiently
        Collection<Entity> entitiesCopy = new ArrayList<>(gameWorld.getEntities());


        // Do physics, yo.
        entitiesCopy.stream()
                    .filter(it -> it.getComponents().keySet().containsAll(physicsSubsystem.getRequiredComponents()))
                    .forEach(it -> physicsSubsystem.operate(delta, it, gameWorld));
        // Do input, dayglo
        entitiesCopy.stream()
                    .filter(it -> it.getComponents().keySet().containsAll(inputSubsystem.getRequiredComponents()) &&
                          (it.getComponents().getInstance(PhysicsComponent.class) instanceof ControlledPhysicsComponent))
                    .forEach(it -> inputSubsystem.operate(delta, it, gameWorld));

        //Camera utility methods
        CameraUtil.lerpCameraToActor(player, bufferCamera);
        CameraUtil.handleZoom(gameWorld, bufferCamera);
        CameraUtil.keepCameraInMap(bufferCamera);

        //Collision checks
        keepEntitiesInMap();

        //Renders HUD
        hud.render(delta);

        //Updates camera
        camera.update();
        bufferCamera.update();
    }

    private void setShaderUniforms() {
        Body body = player.getComponents().getInstance(PhysicsComponent.class);
        Vector2 playerPosition = body.getPosition();
        int xDistFromCamera = (int) ((playerPosition.x - bufferCamera.position.x) / TO_WORLD_UNITS);
        int xOffset = (int) (body.getWidth() / TO_WORLD_UNITS) / 2;
        int playerPixelX = (Gdx.graphics.getWidth() / 2) + xDistFromCamera - xOffset;
        float playerLightX = (float)playerPixelX / Gdx.graphics.getWidth();
        int yDistFromCamera = (int) ((playerPosition.y - bufferCamera.position.y) / TO_WORLD_UNITS);
        int yOffset = (int) ((body.getHeight()) / TO_WORLD_UNITS);
        int playerPixelY = (Gdx.graphics.getHeight() / 2) + yDistFromCamera + yOffset;
        float playerLightY = (float) playerPixelY / Gdx.graphics.getWidth();

        shader.begin();
        shader.setUniformf(AMBIENT_UNIFORM, AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
        shader.setUniformf(POINT_POSITION_UNIFORM, playerLightX, playerLightY, 0.12f);
        shader.setUniformf(POINT_FALLOFF_UNIFORM, POINT_FALLOFF);
        shader.setUniformf(POINT_COLOR_UNIFORM, POINT_COLOR.x, POINT_COLOR.y, POINT_COLOR.z, POINT_INTENSITY);
        shader.end();
    }

    private void drawColorBuffer(float delta) {
        colorBuffer.begin();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(bufferCamera.combined);
        spriteBatch.setShader(defaultShader);

        // TODO Prooooobably should do this more efficiently
        Collection<Entity> entitiesCopy = new ArrayList<>(gameWorld.getEntities());

        //Renders GameWorld background
        gameWorld.render(bufferCamera, delta, spriteBatch);

        // Render Entities
        entitiesCopy.stream()
                    .filter(it -> it.getComponents().keySet().containsAll(renderSubsystem.getRequiredComponents()))
                    .forEach(it -> renderSubsystem.operate(delta, it, gameWorld));

        colorBuffer.end();
    }

    private void drawNormalBuffer(float delta) {
        normalBuffer.begin();

        Gdx.gl.glClearColor(0.5f, 0.5f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(bufferCamera.combined);
        spriteBatch.setShader(defaultShader);

        gameWorld.renderNormal(bufferCamera, delta, spriteBatch);

        normalBuffer.end();
    }

    @Override
    public void resize(int width, int height) {
        shader.begin();
        shader.setUniformf(RESOLUTION_UNIFORM, width, height);
        shader.end();


        camera.setToOrtho(false, width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0.0f);
        camera.update();

        float bufferWidth = width * TO_WORLD_UNITS;
        float bufferHeight = height * TO_WORLD_UNITS;
        bufferCamera.setToOrtho(true, bufferWidth, bufferHeight); // Invert buffer camera because textures are y-up
        bufferCamera.position.set(bufferCamera.viewportWidth / 2, bufferCamera.viewportHeight / 2, 0.0f);
        bufferCamera.update();

        // Resize color buffer
        if(colorBuffer != null) {
            colorBuffer.dispose();
        }
        colorBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);

        if(normalBuffer != null) {
            normalBuffer.dispose();
        }
        normalBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);
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
        colorBuffer.dispose();
        normalBuffer.dispose();
        spriteBatch.dispose();
        hud.dispose();
        gameWorld.dispose();

        LOGGER.info("Disposed");
    }
}
