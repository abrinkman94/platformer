package com.brinkman.platformer.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.entity.Player;
import com.brinkman.platformer.terrain.TMXMap;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/30/2016.
 */
public class CollisionHandler {
    private Vector2 tempVector1 = new Vector2();
    private Vector2 tempVector2 = new Vector2();

    private boolean platform = false;

    public void handleMapCollision(Player player, TMXMap map) {
        Rectangle entityBounds = player.getBounds();
        Rectangle footBounds = player.getBounds();
        footBounds.height = player.getBounds().height * .3f;

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
                        player.getPosition().x = player.getPosition().x - horizontalOverlap;
                        player.setCanJump(true);
                    } else {
                        player.getPosition().x = player.getPosition().x + horizontalOverlap;
                        player.setCanJump(true);
                    }
                } else {
                    if (verticalDistance > 0) {
                        player.getPosition().y = player.getPosition().y - verticalOverlap;
                        player.getVelocity().y = mapBounds.y * TO_WORLD_UNITS;
                        player.setIsGrounded(false);
                        player.setCanJump(false);
                    } else {
                        player.getPosition().y = player.getPosition().y + (verticalOverlap * .99f);
                        player.setIsGrounded(true);
                        player.setCanJump(true);
                    }
                }
            }
        }
    }

    public void checkWaterCollision(Player player, TMXMap map) {
        for (MapObject object : map.getMapObjects("water")) {
            float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
            float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
            float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
            float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

            Rectangle objectBounds = new Rectangle(x, y, width, height);
            Rectangle playerFootBounds = player.getBounds();

            if (playerFootBounds.overlaps(objectBounds)) {
                player.onDeath();
            }
        }
    }

    public void keepPlayerInMap(Player player) {
        float mapLeft = 0;
        float mapRight = TMXMap.mapWidth;

        if (player.getPosition().x <= mapLeft) {
            player.getPosition().x = mapLeft;
        }
        if (player.getPosition().x >= mapRight - player.getWidth() * TO_WORLD_UNITS) {
            player.getPosition().x = mapRight - player.getWidth() * TO_WORLD_UNITS;
        }
    }

    public void debug(TMXMap map, Player player, OrthographicCamera camera) {
        Rectangle playerBounds = player.getBounds();

        for (Rectangle bounds : map.getMapCollisionRectangles()) {
            ShapeRenderer renderer = new ShapeRenderer();
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            renderer.rect(playerBounds.x, playerBounds.y, playerBounds.width, playerBounds.height);
            renderer.end();
        }
    }
}
