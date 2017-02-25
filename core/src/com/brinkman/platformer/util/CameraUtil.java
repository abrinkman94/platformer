package com.brinkman.platformer.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.brinkman.platformer.entity.actor.Actor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.PhysicsComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.Body;

import static com.brinkman.platformer.map.TMXMap.mapHeight;
import static com.brinkman.platformer.map.TMXMap.mapWidth;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * @author Austin Brinkman.
 */
public final class CameraUtil
{
    private CameraUtil() {}

    /**
     * Keeps camera within bounds of map.
     * @param camera OrthographicCamera class.
     */
    public static void keepCameraInMap(OrthographicCamera camera) {
        int mapLeft = 0;
        float mapRight = mapWidth;
        int mapBottom = 0;
        float mapTop = mapHeight;
        float cameraHalfWidth = camera.viewportWidth * 0.5f;
        float cameraHalfHeight = camera.viewportHeight * 0.5f;

        float cameraLeft = camera.position.x - cameraHalfWidth;
        float cameraRight = camera.position.x + cameraHalfWidth;
        float cameraBottom = camera.position.y - cameraHalfHeight;
        float cameraTop = camera.position.y + cameraHalfHeight;

        if(mapWidth < camera.viewportWidth) {
            camera.position.x = mapRight / 2;
        }
        else if(cameraLeft <= mapLeft) {
            camera.position.x = mapLeft + cameraHalfWidth;
        }
        else if(cameraRight >= mapRight) {
            camera.position.x = mapRight - cameraHalfWidth;
        }

        if(mapHeight < camera.viewportHeight) {
            camera.position.y = mapTop / 2;
        }
        if(cameraBottom <= mapBottom) {
            camera.position.y = mapBottom + cameraHalfHeight;
        }
        if(cameraTop >= mapTop) {
            camera.position.y = mapTop - cameraHalfHeight;
        }
    }

    public static void handleZoom(GameWorld world, OrthographicCamera camera) {
        float zoomStep = 0;

        Player player;
        Body body = null;
        Rectangle bounds = null;
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Player) {
                player = (Player) entity;
                body = player.getComponents().getInstance(PhysicsComponent.class);
                if (body != null) {
                    bounds = (Rectangle) body.getBounds();
                }
            }
        }

        if (body != null) {
            Vector2 position = body.getPosition();
            boolean reachedHeightToZoom = (position.y - (bounds.height * 0.5f)) >= 8f;
            boolean isAwayFromMapEdge = (bounds.x > 10) && (bounds.x < (mapWidth - 10));
            boolean zoomOut = reachedHeightToZoom && isAwayFromMapEdge;

            if (zoomOut) {
                if (camera.zoom < 1.3f) {
                    zoomStep = 0.01f;
                }
            } else {
                if (camera.zoom > 1) {
                    zoomStep = -0.01f;
                }
            }

            camera.zoom += zoomStep;
        }
    }

    /**
     * Utility method, centers the OrthographicCamera on, and follows, the Actor.
     * @param actor Actor
     * @param cam OrthographicCamera
     */
    public static void lerpCameraToActor(Actor actor, OrthographicCamera cam) {
        Body body = actor.getComponents().getInstance(PhysicsComponent.class);
        assert body != null;
        Vector2 position = body.getPosition();
        float width = body.getWidth() * 0.5f;
        float height = body.getHeight() * 0.5f;
        cam.position.lerp(new Vector3(position.x + (width * TO_WORLD_UNITS),
                                      position.y + (height * TO_WORLD_UNITS), 0), 0.06f);
    }
}
