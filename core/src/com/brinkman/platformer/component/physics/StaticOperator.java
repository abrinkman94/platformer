package com.brinkman.platformer.component.physics;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.Body;

final class StaticOperator extends AbstractSpecializedOperator<Body> {
    private final Player player;

    StaticOperator(Player player) {this.player = player;}

    @Override
    public void operate(float deltaT, Entity entity, Body body, GameWorld world) {
        handleCollisions(entity, world, player, body);
    }
}
