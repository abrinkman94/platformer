package com.brinkman.platformer.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.*;
import com.brinkman.platformer.entity.actor.*;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.map.TMXMap;
import com.brinkman.platformer.util.Constants;

import java.util.Iterator;
import java.util.Map.Entry;

import static com.brinkman.platformer.util.Constants.NUM_OF_LEVELS;
import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * Created by Austin on 9/30/2016.
 */
public class CollisionHandler {
    private static final Logger LOGGER = new Logger(CollisionHandler.class.getName(), Logger.DEBUG);

    /**
     * Handle's the player's collision with the exit object, as well as the logic behind level changes.
     * @param world GameWorld
     * @param spriteBatch SpriteBatch
     */
    public void handleExitCollision(GameWorld world, SpriteBatch spriteBatch) {
        if (world.getNumberOfCoins() <= 0) {
            Player player = (Player) world.getEntityByValue("player");

            if (!world.getLevel().hasKey() || player.getItems().values().contains(ItemType.KEY)) {
                Rectangle playerBounds = (Rectangle) player.getBounds();

                MapObjects mapObjects = world.getLevel().getMap().getMapObjects("exit");

                for (MapObject object : mapObjects) {
                    float x = object.getProperties().get("x", float.class) * TO_WORLD_UNITS;
                    float y = object.getProperties().get("y", float.class) * TO_WORLD_UNITS;
                    float width = object.getProperties().get("width", float.class) * TO_WORLD_UNITS;
                    float height = object.getProperties().get("height", float.class) * TO_WORLD_UNITS;

                    Rectangle exitBounds = new Rectangle(x, y, width, height);

                    int levelNumber = world.getLevel().getLevelNumber();
                    if (Intersector.overlaps(exitBounds, playerBounds)) {
                        if (world.getLevel().getLevelNumber() < NUM_OF_LEVELS) {
                            levelNumber++;
                            world.setLevel(new Level(levelNumber, spriteBatch));
                            player.reset();

                            //Remove saws and coins from GameWorld
                            for (Iterator<Entry<Entity, String>> it = world.getEntities().entrySet().iterator(); it.hasNext(); ) {
                                Entry<Entity, String> entry = it.next();
                                if (entry.getValue().equalsIgnoreCase("saw")
                                      || entry.getValue().equalsIgnoreCase("coin")
                                      || entry.getValue().equalsIgnoreCase("item")
                                      || entry.getValue().equalsIgnoreCase("static entity")) {
                                    it.remove();
                                }
                            }

                            world.initializeMapObjects();
                        } else {
                            LOGGER.info("No more levels");
                            Gdx.app.exit();
                        }
                    }
                }
            }
        }
    }

    /**
     * Keeps entities within the bounds of the map.
     * @param world GameWorld
     */
    public void keepEntitiesInMap(GameWorld world) {
        float mapLeft = 0;
        float mapRight = TMXMap.mapWidth;

        for (Entity entity : world.getEntities().keySet()) {
            if (!(entity instanceof StaticEntity)) {
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
