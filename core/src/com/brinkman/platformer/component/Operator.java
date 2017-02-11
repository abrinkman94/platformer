package com.brinkman.platformer.component;

import com.brinkman.platformer.entity.Entity;

import java.util.Collection;

/**
 * An {@code Operator} is a single-responsibility subsystem of the Platformer; it takes in an Entity with components of
 * a pre-defined type and operates on them in some fashion.
 *
 * @author Caleb Brinkman
 */
public interface Operator {
    Collection<Class<? extends RootComponent>> getRequiredComponents();

    /**
     * Operate on the given {@link Entity}.
     *
     * @param deltaT The amount of time (in seconds) in order to operate on.
     * @param entity The {@link Entity} on which to operate.
     * @param allEntities The collection of all entities in the game world.
     */
    void operate(float deltaT, Entity entity, Collection<Entity> allEntities);
}
