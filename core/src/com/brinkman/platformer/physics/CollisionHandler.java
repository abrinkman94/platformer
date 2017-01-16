package com.brinkman.platformer.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.StaticEntity;
import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.entity.actor.Enemy;
import com.brinkman.platformer.entity.actor.Exit;
import com.brinkman.platformer.map.TMXMap;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/30/2016.
 */
public class CollisionHandler {
    private static final Logger LOGGER = new Logger(CollisionHandler.class.getName(), Logger.DEBUG);

    /**
     * Keeps entities within the bounds of the map.
     * @param world GameWorld
     */
    public void keepEntitiesInMap(GameWorld world) {
        float mapLeft = 0;
        float mapRight = TMXMap.mapWidth;

        for (Entity entity : world.getEntities().keySet()) {
            if (!(entity instanceof StaticEntity) && !(entity instanceof Exit)) {
                Actor actor = (Actor) entity;

                if (actor.getPosition().x <= mapLeft) {
                    actor.getPosition().x = mapLeft;
                    if (actor instanceof Enemy) {
                        actor.getVelocity().x = -actor.getVelocity().x;
                    }
                }
                if (actor.getPosition().x >= (mapRight - (actor.getWidth() * TO_WORLD_UNITS))) {
                    actor.getPosition().x = mapRight - (actor.getWidth() * TO_WORLD_UNITS);
                    if (actor instanceof Enemy) {
                        actor.getVelocity().x = -actor.getVelocity().x;
                    }
                }
            }
        }
    }

    public void debug(GameWorld world, OrthographicCamera camera) {
        Entity player = world.getEntityByValue("player");
        Rectangle playerBounds = (Rectangle) player.getBounds();

     /*   for (Rectangle bounds : world.getLevel().getMap().getMapCollisionRectangles()) {
            ShapeRenderer renderer = new ShapeRenderer();
            renderer.setProjectionMatrix(camera.combined);
            renderer.begin(ShapeType.Line);
            renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            renderer.rect(playerBounds.x, playerBounds.y, playerBounds.width, playerBounds.height);
            renderer.end();
        } */

        for (Entity entity : world.getEntities().keySet()) {
            if (entity instanceof StaticEntity) {
                Rectangle sawBounds = (Rectangle) entity.getBounds();
                ShapeRenderer renderer = new ShapeRenderer();
                renderer.setProjectionMatrix(camera.combined);
                renderer.begin(ShapeType.Line);
                renderer.rect(sawBounds.x, sawBounds.y, sawBounds.width, sawBounds.height);
                renderer.end();
            }
        }
    }
}
