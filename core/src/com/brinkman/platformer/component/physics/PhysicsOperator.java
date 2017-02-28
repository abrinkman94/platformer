package com.brinkman.platformer.component.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.Operator;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Exit;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.physics.ControlledBody;
import com.brinkman.platformer.physics.MotileBody;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.brinkman.platformer.util.Constants.NUM_OF_LEVELS;

/**
 * @author Caleb Brinkman
 */
public class PhysicsOperator implements Operator {
    private static final Vector2 TEMP_POSITION = new Vector2();
    private static final Vector2 TEMP_VELOCITY = new Vector2();
    private static final int WALL_BOUNCE = 6;
    private static final float JERK_RATE = 0.1f;
    private static final float FRICTION_DECELARATION = 0.8f;
    private static final float AIR_DECELARATION = 0.25f;

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

        ControlledBody body = (ControlledBody) entity.getComponents().getInstance(PhysicsComponent.class);
        assert body != null;

        // Do some storing and preparation
        TEMP_POSITION.set(body.getPosition());
        TEMP_VELOCITY.set(body.getVelocity());

        handleGravity(body);

        handleAcceleration(deltaT, body);

        handleCollisions(entity, world, player, body);

        // Do some resetting
        // If the y-position hasn't changed, we're grounded.
        body.setGrounded(TEMP_POSITION.y == body.getPosition().y);
    }

    private void handleAcceleration(float deltaT, ControlledBody body) {
        float xVelocity = body.getVelocity().x;
        // TODO Floating point errors?
        if(body.getAcceleration().x == 0) {
            xVelocity = getInertialVelocity(body);
        } else {
            xVelocity += body.getAcceleration().x;
            xVelocity = Math.signum(xVelocity) * Math.min(body.getMoveSpeed(), Math.abs(xVelocity));
        }

        xVelocity = handleJump(body, xVelocity);

        body.getVelocity().x = xVelocity;
        body.getPosition().x += body.getVelocity().x * deltaT;
        body.getPosition().y += body.getVelocity().y * deltaT;

        // Zero out acceleration
        body.getAcceleration().x = 0.0f;
    }

    private float handleJump(ControlledBody body, float xVelocity) {
        boolean canJump = body.isGrounded() || body.isTouchingLeftWall() || body.isTouchingRightWall();
        if (body.isJumping() && canJump && !body.justJumped()) {
            Vector2 velocity = body.getVelocity();
            velocity.y = body.getJumpVelocity();

            //Wall bounce
            if (!body.isGrounded()) {
                if (body.isTouchingRightWall()) {
                    xVelocity = velocity.x - (WALL_BOUNCE + 2);
                } else if (body.isTouchingLeftWall()) {
                    xVelocity = velocity.x + (WALL_BOUNCE + 2);
                }
            }
            body.setGrounded(false);
            body.setJustJumped(true);
        }
        return xVelocity;
    }

    private float getInertialVelocity(MotileBody body) {
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

    private void handleGravity(MotileBody body) {
        if(body.isAffectedByGravity()) {
            Vector2 velocity = body.getVelocity();
            if (velocity.y > body.getMaxFallSpeed()) {
                velocity.y -= body.getGravityAcceleration();
            }
        }
    }

    private void handleCollisions(Entity entity, GameWorld world, Player player, ControlledBody body) {
        body.setTouchingLeftWall(false);
        body.setTouchingRightWall(false);
        Collection<Entity> colliders = findCollidingEntities(entity, world);
        colliders.forEach(collider -> {
            Body otherBody = collider.getComponents().getInstance(PhysicsComponent.class);
            if (otherBody != null) {
                body.triggerCollisionListeners(collider, otherBody);
                if (body.isRemovedOnCollision()) {
                    handleRemoval(entity, player, world);
                }
                if(otherBody.shouldCollideWith(entity)) {
                    otherBody.triggerCollisionListeners(entity, body);

                    if(otherBody.isRemovedOnCollision()) {
                        handleRemoval(collider, player, world);
                    }
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
