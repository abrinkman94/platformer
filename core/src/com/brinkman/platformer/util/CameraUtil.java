package com.brinkman.platformer.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.brinkman.platformer.entity.Actor;
import com.badlogic.gdx.math.MathUtils;
import com.brinkman.platformer.entity.Actor;
import com.brinkman.platformer.terrain.TMXMap;

import static com.brinkman.platformer.terrain.TMXMap.mapHeight;
import static com.brinkman.platformer.terrain.TMXMap.mapWidth;
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
     * @return returns true.
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
            camera.position.y = mapTop / 2 + 2;
        }
        else if(cameraBottom <= mapBottom) {
            camera.position.y = mapBottom + cameraHalfHeight;
        }
        else if(cameraTop >= mapTop) {
            camera.position.y = mapTop - cameraHalfHeight;
        }
    }

    public static void centerCameraOnActor(Actor actor, OrthographicCamera cam) {
        cam.position.set(actor.getPosition().x + ((actor.getWidth() * 0.5f) * TO_WORLD_UNITS),
              cam.position.y, 0);
    }
}