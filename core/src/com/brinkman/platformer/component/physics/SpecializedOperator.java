package com.brinkman.platformer.component.physics;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.Body;

interface SpecializedOperator<T extends Body> {
    void operate(float deltaT, Entity entity, T body, GameWorld world);

    void handleCollisions(Entity entity, GameWorld world, Player player, T body);
}
