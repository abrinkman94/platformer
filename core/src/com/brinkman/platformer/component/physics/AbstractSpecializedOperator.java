package com.brinkman.platformer.component.physics;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.Body;

import java.util.Collection;

/**
 * @author Caleb Brinkman
 */
abstract class AbstractSpecializedOperator<T extends Body> implements SpecializedOperator<T> {
    @Override
    public void handleCollisions(Entity entity, GameWorld world, Player player, T body) {

        Collection<Entity> colliders = PhysicsUtilities.findCollidingEntities(entity, world);
        colliders.forEach(collider -> {
            Body otherBody = collider.getComponents().getInstance(PhysicsComponent.class);
            if (otherBody != null) {
                body.triggerCollisionListeners(collider, otherBody);
                if (body.isRemovedOnCollision()) {
                    PhysicsUtilities.handleRemoval(entity, player, world);
                }
                if(otherBody.shouldCollideWith(entity)) {
                    otherBody.triggerCollisionListeners(entity, body);

                    if(otherBody.isRemovedOnCollision()) {
                        PhysicsUtilities.handleRemoval(collider, player, world);
                    }
                }
            }
        });
    }
}
