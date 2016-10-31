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
import com.brinkman.platformer.entity.*;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.terrain.TMXMap;
import com.brinkman.platformer.util.Constants;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/30/2016.
 */
public class CollisionHandler {
    private final Vector2 tempVector1 = new Vector2();
    private final Vector2 tempVector2 = new Vector2();

    /**
     * Handles the player's collision with "ground".
     * @param player Player
     * @param map TMXMap
     */
    public void handleMapCollision(Player player, TMXMap map) {
        Rectangle entityBounds = player.getBounds();
        player.setIsGrounded(false);
        player.setTouchingLeftWall(false);
        player.setTouchingRightWall(false);

        for (Rectangle mapBounds : map.getMapCollisionRectangles()) {

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
     * @param player Player
     * @param saws Array Saw
     */
    public void handleSawCollision(Player player, Array<Saw> saws) {
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
     * @param player Player
     * @param coins Array<Coin>
     */
    public void handleCoinCollision(Player player, Array<Coin> coins) {
        Rectangle playerBounds = player.getBounds();

        for (Coin coin : coins) {
            Rectangle coinBounds = new Rectangle(coin.getPosition().x, coin.getPosition().y,
                  coin.getWidth(), coin.getHeight());

            if (playerBounds.overlaps(coinBounds)) {
                coin.setCollected(true);
                coin.dispose();
                coins.removeValue(coin, true);
            }
        }
    }

    public void handleEnemyCollision(Player player, Enemy enemy, TMXMap map) {
        Rectangle playerBounds = player.getBounds();
        Rectangle enemyBounds = enemy.getBounds();

        if (playerBounds.overlaps(enemyBounds)) {
            player.handleDeath();
        }

        for (Rectangle mapBounds : map.getMapCollisionRectangles()) {
            if (enemyBounds.overlaps(mapBounds)) {
                enemy.getVelocity().x = -enemy.getVelocity().x;
            }
        }
    }

    public void handleExitCollision(Level level, Player player, Array<Coin> coins, SpriteBatch spriteBatch) {
        if (coins.size <= 0) {
            Rectangle playerBounds = player.getBounds();

            MapObjects mapObjects = level.getMap().getMapObjects("exit");

            for (MapObject object : mapObjects) {
                float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
                float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
                float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

                Rectangle exitBounds = new Rectangle(x, y, width, height);

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
