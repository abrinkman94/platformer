package com.brinkman.platformer.component.physics;

import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.MotileBody;

public abstract class AbstractMotileOperator<T extends MotileBody> extends AbstractSpecializedOperator<T>  {
    private static final Vector2 TEMP_POSITION = new Vector2();
    private static final Vector2 TEMP_VELOCITY = new Vector2();
    private final Player player;

    AbstractMotileOperator(Player player) {this.player = player;}

    @Override
    public void operate(float deltaT, Entity entity, T body, GameWorld world) {
        TEMP_POSITION.set(body.getPosition());
        TEMP_VELOCITY.set(body.getVelocity());

        handleGravity(body);

        handleAcceleration(deltaT, body);

        handleCollisions(entity, world, player, body);

        // Do some resetting
        // If the y-position hasn't changed, we're grounded.
        body.setGrounded(TEMP_POSITION.y == body.getPosition().y);
    }

    private void handleAcceleration(float deltaT, T body) {
        float xVelocity = body.getVelocity().x;
        // TODO Floating point errors?
        if (body.getAcceleration().x == 0) {
            xVelocity = PhysicsUtilities.getInertialVelocity(body);
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

    private void handleGravity(T body) {
        if (body.isAffectedByGravity()) {
            Vector2 velocity = body.getVelocity();
            if (velocity.y > body.getMaxFallSpeed()) {
                velocity.y -= body.getGravityAcceleration();
            }
        }
    }

    abstract float handleJump(T body, float xVelocity);
}
