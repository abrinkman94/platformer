package com.brinkman.platformer.component.physics;

import com.badlogic.gdx.Gdx;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Exit;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.physics.MotileBody;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.brinkman.platformer.util.Constants.NUM_OF_LEVELS;

enum PhysicsUtilities {
    ;
    private static final float FRICTION_DECELARATION = 0.8f;
    private static final float AIR_DECELARATION = 0.25f;

    static float getInertialVelocity(MotileBody body) {
        float xVelocity = body.getVelocity().x;
        float xVelSign = Math.signum(xVelocity);
        xVelocity = Math.abs(xVelocity);
        if (body.isGrounded()) {
            xVelocity -= (xVelocity == 0.0f) ? 0.0f : FRICTION_DECELARATION;
        } else {
            xVelocity -= (xVelocity == 0.0f) ? 0.0f : AIR_DECELARATION;
        }

        xVelocity = (xVelocity < AIR_DECELARATION) ? 0.0f : xVelocity;
        xVelocity *= xVelSign;
        return xVelocity;
    }

    static void handleRemoval(Entity entity, Player player, GameWorld world) {
        world.removeEntity(entity);
        if (entity instanceof Player) {
            // TODO Handle Game Over
        } else if (entity instanceof Exit) {
            int levelNumber = world.getLevel().getLevelNumber();

            if (world.getLevel().getLevelNumber() < NUM_OF_LEVELS) {
                levelNumber++;
                world.setLevel(new Level(levelNumber));

                player.reset();
                clearWorld(world);

                world.initializeMapObjects();
            } else {
                Gdx.app.exit();
            }
        }
    }

    private static void clearWorld(GameWorld world) {
        Collection<Entity> entitiesToRemove
                = world.getEntities()
                       .stream()
                       .filter(it -> !(it instanceof Player))
                       .collect(Collectors.toList());

        entitiesToRemove.stream()
                        .filter(Entity.class::isInstance)
                        .map(Entity.class::cast)
                        .forEach(world::removeEntity);
    }

    static Collection<Entity> findCollidingEntities(Entity entity, GameWorld world) {
        Collection<Entity> colliding = new LinkedList<>();
        world.getEntities().stream()
             .filter(otherEntity -> areColliding(entity, otherEntity))
             .forEach(colliding::add);

        return colliding;
    }

    private static boolean areColliding(Entity entity, Entity otherEntity) {
        Body body = entity.getComponents().getInstance(PhysicsComponent.class);
        Body otherBody = otherEntity.getComponents().getInstance(PhysicsComponent.class);

        if (Objects.equals(entity, otherEntity) || (body == null) || (otherBody == null)) {
            return false;
        }

        boolean isColliding = body.shouldCollideWith(otherEntity);
        isColliding &= body.intersects(otherBody);

        return isColliding;
    }
}
