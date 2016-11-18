package com.brinkman.platformer.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.*;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.terrain.TMXMap;
import com.brinkman.platformer.util.Constants;

import java.util.Iterator;
import java.util.Map;

import static com.brinkman.platformer.util.Constants.NUM_OF_LEVELS;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/30/2016.
 */
public class CollisionHandler {
    private final Vector2 tempVector1 = new Vector2();
    private final Vector2 tempVector2 = new Vector2();

    private boolean zoomOut = false;
    private boolean zoomIn = false;

    private static final Logger LOGGER = new Logger(CollisionHandler.class.getName(), Logger.DEBUG);
    /**
     * Handles the player's collision with "ground".
     */
    public void handleMapCollision(GameWorld world) {
        Player player = (Player) world.getEntityByValue("player");
        Rectangle entityBounds = world.getEntityByValue("player").getBounds();
        player.setIsGrounded(false);
     //   ((Player)world.getEntityByValue("player")).setCanJump(false);
        player.setTouchingLeftWall(false);
        player.setTouchingRightWall(false);

        for (Rectangle mapBounds : world.getLevel().getMap().getMapCollisionRectangles()) {

            // Simple collision check
            if (entityBounds.overlaps(mapBounds)) {
                // Get the centers of the Entity AABB and map AABB; place in tempVector1 and tempVector2 respectively
                entityBounds.getCenter(tempVector1);
                mapBounds.getCenter(tempVector2);
                // Get the absolute value of horizontal overlap between the entity and map tile
                // Save signed value of distance for later
                float horizontalDistance = tempVector2.x - tempVector1.x;
                float entityHalfWidth = entityBounds.width / 2;
                float mapHalfWidth = mapBounds.width / 2;
                float horizontalOverlap = (entityHalfWidth + mapHalfWidth) - Math.abs(horizontalDistance);

                // Get the absolute value of the vertical overlap between the entity and the map time
                // Save signed value of distance for later
                float verticalDistance = tempVector2.y - tempVector1.y;
                float entityHalfHeight = entityBounds.height / 2;
                float mapHalfHeight = mapBounds.height / 2;
                float verticalOverlap = (entityHalfHeight + mapHalfHeight) - Math.abs(verticalDistance);

                // Move the entity on the axis which has the least overlap.
                // The direction that the entity will move is determined by the sign of the distance between the centers
                if (horizontalOverlap < verticalOverlap) {
                    if (horizontalDistance > 0) {
                        player.setTouchingRightWall(true);
                        player.getPosition().x = player.getPosition().x - horizontalOverlap;
                    } else {
                        player.setTouchingLeftWall(true);
                        player.getPosition().x = player.getPosition().x + horizontalOverlap;
                    }
                    player.setCanJump(true);
                } else {
                    if (verticalDistance > 0) {
                        player.getPosition().y = player.getPosition().y - verticalOverlap;
                        player.getVelocity().y = -Constants.GRAVITY;
                        player.setCanJump(false);
                    } else {
                        player.getPosition().y = player.getPosition().y + verticalOverlap;
                        player.setIsGrounded(true);
                        player.setCanJump(true);
                    }
                }
            }
        }
    }

    /**
     * Handles player's collision with water.
     * @param saws Array Saw
     */
    public void handleSawCollision(Array<Saw> saws, GameWorld world) {
        Entity player = world.getEntityByValue("player");
        Rectangle playerBounds = player.getBounds();

        for (Saw saw : saws) {
            Rectangle sawBounds = saw.getBounds();

            if (playerBounds.overlaps(sawBounds)) {
                player.handleDeath();
            }
        }
    }

    /**
     * Handles the player's collision with coin objects.
     */
    public void handleCoinCollision(Array<Coin> coins, GameWorld world) {
        Entity player = world.getEntityByValue("player");
        Rectangle playerBounds = player.getBounds();

        for (Coin coin : coins) {
            Rectangle coinBounds = new Rectangle(coin.getPosition().x, coin.getPosition().y,
                    coin.getWidth(), coin.getHeight());

            if (playerBounds.overlaps(coinBounds)) {
                coin.setCollected(true);
                coins.removeValue(coin, true);
                world.removeEntity(coin);
                coin.dispose();
            }
        }
    }

    /**
     * Handle's the player's collision with items.
     * @param world GameWorld
     */
    public void handleItemCollision(GameWorld world) {
        Actor player = (Actor) world.getEntityByValue("player");
        Rectangle playerBounds = player.getBounds();

        for (Iterator<Map.Entry<Entity, String>> it = world.getEntities().entrySet().iterator(); it.hasNext();) {
            Map.Entry<Entity, String> entry = it.next();

            if (entry.getValue().equalsIgnoreCase("item")) {
                Rectangle itemBounds = entry.getKey().getBounds();

                if (playerBounds.overlaps(itemBounds)) {
                    it.remove();

                    if (((Item)entry.getKey()).getItemType() == ItemType.LIFE) {
                        player.setLives(player.getLives() + 1);
                    }
                }
            }
        }
    }

    /**
     * Handle's the player's collision with enemies.
     * @param world GameWorld
     */
    public void handleEnemyCollision(GameWorld world) {
        Rectangle playerBounds = world.getEntityByValue("player").getBounds();
        Rectangle enemyBounds = world.getEntityByValue("enemy").getBounds();

        if (playerBounds.overlaps(enemyBounds)) {
            world.getEntityByValue("player").handleDeath();
            ((Actor)world.getEntityByValue("enemy")).getVelocity().x = -((Actor)world.getEntityByValue("enemy")).getVelocity().x;
        }

        for (Rectangle mapBounds : world.getLevel().getMap().getMapCollisionRectangles()) {
            if (enemyBounds.overlaps(mapBounds)) {
                ((Actor)world.getEntityByValue("enemy")).getVelocity().x = -((Actor)world.getEntityByValue("enemy")).getVelocity().x;
            }
        }
    }

    /**
     * Handle's the player's collision with the exit object, as well as the logic behind level changes.
     * @param world GameWorld
     * @param coins Array<Coin>
     * @param saws Array<Saw>
     * @param spriteBatch SpriteBatch
     */
    public void handleExitCollision(GameWorld world, Array<Coin> coins, Array<Saw> saws, SpriteBatch spriteBatch) {
        if (coins.size <= 0) {
            Player player = (Player) world.getEntityByValue("player");
            Rectangle playerBounds = player.getBounds();

            MapObjects mapObjects = world.getLevel().getMap().getMapObjects("exit");

            for (MapObject object : mapObjects) {
                float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
                float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
                float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

                Rectangle exitBounds = new Rectangle(x, y, width, height);

                int levelNumber = world.getLevel().getLevelNumber();
                if (playerBounds.overlaps(exitBounds)) {
                    if (world.getLevel().getLevelNumber() < NUM_OF_LEVELS) {
                        levelNumber++;
                        world.setLevel(new Level(levelNumber, spriteBatch));
                        player.reset();

                        //Clear Array<Saw> saws and Array<Coin> coins
                        saws.clear();
                        coins.clear();

                        //Remove saws and coins from GameWorld
                        for (Iterator<Map.Entry<Entity, String>> it = world.getEntities().entrySet().iterator(); it.hasNext();) {
                            Map.Entry<Entity, String> entry = it.next();
                            if (entry.getValue().equalsIgnoreCase("saw") || entry.getValue().equalsIgnoreCase("coin")
                                    || entry.getValue().equalsIgnoreCase("item")) {
                                it.remove();
                            }
                        }

                        world.initializeMapObjects(spriteBatch, coins, saws);
                    } else {
                        LOGGER.info("No more levels");
                        Gdx.app.exit();
                    }
                }
            }
        }
    }

    public void handleZoomCollision(GameWorld world, OrthographicCamera camera) {
        Player player = ((Player)world.getEntityByValue("player"));

        Rectangle playerBounds = player.getBounds();

        MapObject zoomOutObject = world.getLevel().getMap().getMapObjects("zoom out").get(0);
        MapObject zoomInObject = world.getLevel().getMap().getMapObjects("zoom in").get(0);

        Rectangle zoomOutBounds = world.getLevel().getMap().getMapObjectBounds(zoomOutObject);
        Rectangle zoomInBounds = world.getLevel().getMap().getMapObjectBounds(zoomInObject);

        float zoomStep = 0;

        if (playerBounds.overlaps(zoomOutBounds)) {
            zoomOut = true;
            zoomIn = false;
        } else if (playerBounds.overlaps(zoomInBounds) && camera.zoom > 1) {
            zoomIn = true;
            zoomOut = false;
        }

        if (zoomOut) {
            if (camera.zoom <= 1.3f) {
                zoomStep = 0.01f;
            } else {
                zoomStep = 0;
            }
        } else if (zoomIn && camera.zoom > 1) {
            if (camera.zoom > 1) {
                zoomStep = -0.01f;
            } else {
                zoomStep = 0;
            }
        }

        camera.zoom += zoomStep;
    }

    /**
     * Keeps the player within the bounds of the map.
     * @param actor Player
     */
    public void keepActorInMap(Actor actor) {
        float mapLeft = 0;
        float mapRight = TMXMap.mapWidth;

        if (actor.getPosition().x <= mapLeft) {
            actor.getPosition().x = mapLeft;
            if (actor instanceof Enemy) {
                actor.getVelocity().x = -actor.getVelocity().x;
            }
        }
        if (actor.getPosition().x >= mapRight - actor.getWidth() * TO_WORLD_UNITS) {
            actor.getPosition().x = mapRight - actor.getWidth() * TO_WORLD_UNITS;
            if (actor instanceof Enemy) {
                actor.getVelocity().x = -actor.getVelocity().x;
            }
        }
    }

    public void debug(TMXMap map, Player player, OrthographicCamera camera, Array<Saw> saws) {
        Rectangle playerBounds = player.getBounds();

        for (Rectangle bounds : map.getMapCollisionRectangles()) {
            ShapeRenderer renderer = new ShapeRenderer();
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            renderer.rect(playerBounds.x, playerBounds.y, playerBounds.width, playerBounds.height);
            renderer.end();
        }

        for (Saw saw : saws) {
            Rectangle sawBounds = saw.getBounds();
            ShapeRenderer renderer = new ShapeRenderer();
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.rect(sawBounds.x, sawBounds.y, sawBounds.width, sawBounds.height);
            renderer.end();
        }
    }
}
