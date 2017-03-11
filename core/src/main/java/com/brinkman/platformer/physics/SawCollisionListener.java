package com.brinkman.platformer.physics;

import com.brinkman.platformer.entity.actor.Actor;
import com.brinkman.platformer.entity.actor.Saw;

/**
 * @author Caleb Brinkman
 */
public class SawCollisionListener implements CollisionListener<Saw> {

    private final Actor actor;

    public SawCollisionListener(Actor actor) { this.actor = actor; }

    @Override
    public void onCollision(Saw other) { }
}
