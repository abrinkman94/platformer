package com.brinkman.platformer.component.physics;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.physics.MotileBody;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

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
        }
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
