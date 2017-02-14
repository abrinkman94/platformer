package com.brinkman.platformer.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Exit;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.physics.Body;

import java.util.*;
import java.util.stream.Collectors;

import static com.brinkman.platformer.util.Constants.NUM_OF_LEVELS;

/**
 * @author Caleb Brinkman
 */
public class PhysicsOperator implements Operator {
    private final Collection<Class<? extends RootComponent>> requiredComponents;

    public PhysicsOperator() {
        requiredComponents = new LinkedList<>();
        requiredComponents.add(PhysicsComponent.class);
    }

    @Override
    public Collection<Class<? extends RootComponent>> getRequiredComponents() {
        return Collections.unmodifiableCollection(requiredComponents);
    }

    @Override
    public void operate(float deltaT, Entity entity, GameWorld world) {
        Player player = world.getEntities().stream()
                             .filter(it -> it instanceof Player)
                             .map(Player.class::cast)
                             .findFirst()
                             .get();

        Body body = entity.getComponents().getInstance(PhysicsComponent.class);
        assert body != null;

        // Handle inertia
        handleInertia(deltaT, body);

        handleGravity(body);

        handleCollisions(entity, world, player, body);
    }

    private void handleInertia(float deltaT, Body body) {
        body.getPosition().y += body.getVelocity().y * deltaT;
    }

    private void handleGravity(Body body) {
        if(body.isAffectedByGravity()) {
            Vector2 velocity = body.getVelocity();
            if (body.isGrounded()) {
                velocity.y = 0;
            } else {
                if (velocity.y > body.getMaxFallSpeed()) {
                    velocity.y -= body.getGravityAcceleration();
                }
            }
        }
    }

    private void handleCollisions(Entity entity, GameWorld world, Player player, Body body) {
        Collection<Entity> colliders = findCollidingEntities(entity, world);
        colliders.forEach(collider -> {
            Body otherBody = collider.getComponents().getInstance(PhysicsComponent.class);
            if (otherBody != null) {
                body.triggerCollisionListeners(collider, otherBody);
                if (body.isRemovedOnCollision()) {
                    handleRemoval(entity, player, world);
                }
            }
        });
    }

    private static void handleRemoval(Entity entity, Player player, GameWorld world) {
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

    private static Collection<Entity> findCollidingEntities(Entity entity, GameWorld world) {
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
