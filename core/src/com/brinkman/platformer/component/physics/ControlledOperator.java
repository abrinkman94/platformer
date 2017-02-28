package com.brinkman.platformer.component.physics;

import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.ControlledBody;

final class ControlledOperator extends AbstractMotileOperator<ControlledBody> {
    private static final int WALL_BOUNCE = 6;

    ControlledOperator(Player player) { super(player); }

    @Override
    public float handleJump(ControlledBody body, float xVelocity) {
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
}
