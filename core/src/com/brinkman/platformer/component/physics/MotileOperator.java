package com.brinkman.platformer.component.physics;

import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.MotileBody;

final class MotileOperator extends AbstractMotileOperator<MotileBody> {

    MotileOperator(Player player) { super(player); }

    public float handleJump(MotileBody body, float xVelocity) { return xVelocity; }
}
