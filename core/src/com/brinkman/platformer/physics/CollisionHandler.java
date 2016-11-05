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
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.*;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.terrain.TMXMap;
import com.brinkman.platformer.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static com.brinkman.platformer.util.Constants.NUM_OF_LEVELS;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/30/2016.
 */
public class CollisionHandler {
    private final Vector2 tempVector1 = new Vector2();
    private final Vector2 tempVector2 = new Vector2();

    /**
     * Handles the player's collision with "ground".
     */
    public void handleMapCollision(GameWorld world) {
        Rectangle entityBounds = world.getEntity("player").getBounds();
        ((Player)world.getEntity("player")).setIsGrounded(false);
        ((Player)world.getEntity("player")).setTouchingLeftWall(false);
        ((Player)world.getEntity("player")).setTouchingRightWall(false);

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
                        ((Player)world.getEntity("player")).setTouchingRightWall(true);
                        ((Player)world.getEntity("player")).getPosition().x =
                                ((Player)world.getEntity("player")).getPosition().x - horizontalOverlap;
                    } else {
                        ((Player)world.getEntity("player")).setTouchingLeftWall(true);
                        ((Player)world.getEntity("player")).getPosition().x =
                                ((Player)world.getEntity("player")).getPosition().x + horizontalOverlap;
                    }
                    ((Player)world.getEntity("player")).setCanJump(true);
                } else {
                    if (verticalDistance > 0) {
                        ((Player)world.getEntity("player")).getPosition().y =
                                ((Player)world.getEntity("player")).getPosition().y - verticalOverlap;
                        ((Player)world.getEntity("player")).getVelocity().y = -Constants.GRAVITY;
                        ((Player)world.getEntity("player")).setCanJump(false);
                    } else {
                        ((Player)world.getEntity("player")).getPosition().y =
                                ((Player)world.getEntity("player")).getPosition().y + verticalOverlap;
                        ((Player)world.getEntity("player")).setIsGrounded(true);
                        ((Player)world.getEntity("player")).setCanJump(true);
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
        Rectangle playerBounds = world.getEntity("player").getBounds();

        for (Saw saw : saws) {
            Rectangle sawBounds = saw.getBounds();

            if (playerBounds.overlaps(sawBounds)) {
                world.getEntity("player").handleDeath();
            }
        }
    }

    /**
     * Handles the player's collision with coin objects.
     */
    public void handleCoinCollision(Array<Coin> coins, GameWorld world) {
        Rectangle playerBounds = world.getEntity("player").getBounds();

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

    public void handleEnemyCollision(GameWorld world) {
        Rectangle playerBounds = world.getEntity("player").getBounds();
        Rectangle enemyBounds = world.getEntity("enemy").getBounds();

        if (playerBounds.overlaps(enemyBounds)) {
            world.getEntity("player").handleDeath();
            ((Actor)world.getEntity("enemy")).getVelocity().x = -((Actor)world.getEntity("enemy")).getVelocity().x;
        }

        for (Rectangle mapBounds : world.getLevel().getMap().getMapCollisionRectangles()) {
            if (enemyBounds.overlaps(mapBounds)) {
                ((Actor)world.getEntity("enemy")).getVelocity().x = -((Actor)world.getEntity("enemy")).getVelocity().x;
            }
        }
    }

    public void handleExitCollision(GameWorld world, Array<Coin> coins, Array<Saw> saws, SpriteBatch spriteBatch) {
        if (coins.size <= 0) {
            Rectangle playerBounds = world.getEntity("player").getBounds();

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
                        ((Player)world.getEntity("player")).reset();

                        //Clear Array<Saw> saws and Array<Coin> coins
                        saws.clear();
                        coins.clear();

                        //Remove saws and coins from GameWorld
                        for (Iterator<Map.Entry<Entity, String>> it = world.getEntities().entrySet().iterator(); it.hasNext();) {
                            Map.Entry<Entity, String> entry = it.next();
                            if (entry.getValue().equalsIgnoreCase("saw") || entry.getValue().equalsIgnoreCase("coin")) {
                                it.remove();
                            }
                        }

                        //Look for and add new coins to Array<Coin> coins and GameWorld
                        for (MapObject mapCoin : world.getLevel().getMap().getMapObjects("coins")) {
                            float coinX = mapCoin.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                            float coinY = mapCoin.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                            Coin coin = new Coin(spriteBatch, coinX, coinY + 1);
                            coins.add(coin);
                            world.addEntity(coin);
                        }

                        //Look for and add new saws to Array<Saw> saws and GameWorld
                        for (MapObject mapSaw : world.getLevel().getMap().getMapObjects("saw")) {
                            float sawX = mapSaw.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                            float sawY = mapSaw.getProperties().get("y", float.class) * TO_WORLD_UNITS;

                            Saw saw = new Saw(spriteBatch, sawX, sawY);
                            saws.add(saw);
                            world.addEntity(saw);
                        }
                    } else {
                        Gdx.app.exit();
                    }
                }
            }
        }
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
