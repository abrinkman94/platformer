package com.brinkman.platformer.action;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.component.status.StatusComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.entity.actor.SimpleEnemy;
import com.brinkman.platformer.physics.ControlledBody;

import java.util.function.BiConsumer;

public class MeleeAttackAction implements BiConsumer<Entity, GameWorld> {
    @Override
    public void accept(Entity entity, GameWorld world) {
        StatusComponent stats = entity.getComponents().getInstance(StatusComponent.class);
        ControlledBody body = (ControlledBody) entity.getComponents().getInstance(PhysicsComponent.class);

        for (Entity otherEntity : world.getEntities()) {
            if (!(otherEntity instanceof Player)) {
                PhysicsComponent otherBody = otherEntity.getComponents().getInstance(PhysicsComponent.class);
                StatusComponent otherStats = otherEntity.getComponents().getInstance(StatusComponent.class);

                if ((otherBody != null) && (otherStats != null)) {
                    float distanceSquared = body.getPosition().dst2(otherBody.getPosition());
                    float attackReachSquared = 1.0f; // FIXME hardcoded, should probably be in StatusComponent?

                    if (distanceSquared <= attackReachSquared) {
                        boolean facingTarget = body.isFacingRight()
                              ? (body.getPosition().x <= otherBody.getPosition().x)
                              : (body.getPosition().x >= otherBody.getPosition().x);

                        if (facingTarget) {
                            int currentHealth = otherStats.getCurrentHealth();
                            currentHealth -= stats.getMeleeDamage();
                            otherStats.setCurrentHealth(currentHealth);
                        }
                    }
                }
            }
        }
    }
}
