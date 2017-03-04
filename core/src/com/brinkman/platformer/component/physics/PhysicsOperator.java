package com.brinkman.platformer.component.physics;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.Operator;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.physics.Body;
import com.brinkman.platformer.physics.ControlledBody;
import com.brinkman.platformer.physics.MotileBody;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class PhysicsOperator implements Operator {

    private final SpecializedOperator<Body> staticOperator;
    private final SpecializedOperator<MotileBody> motileOperator;
    private final SpecializedOperator<ControlledBody> controlledOperator;

    private final Collection<Class<? extends RootComponent>> requiredComponents;

    public PhysicsOperator(Player player) {
        requiredComponents = new LinkedList<>();
        requiredComponents.add(PhysicsComponent.class);
        staticOperator = new StaticOperator(player);
        motileOperator = new MotileOperator(player);
        controlledOperator = new ControlledOperator(player);
    }

    @Override
    public Collection<Class<? extends RootComponent>> getRequiredComponents() {
        return Collections.unmodifiableCollection(requiredComponents);
    }

    @Override
    public void operate(float deltaT, Entity entity, GameWorld world) {
        Body body = entity.getComponents().getInstance(PhysicsComponent.class);
        assert body != null;

        if (body instanceof ControlledBody) {
            controlledOperator.operate(deltaT, entity, (ControlledBody) body, world);
        } else if (body instanceof MotileBody) {
            motileOperator.operate(deltaT, entity, (MotileBody) body, world);
        } else {
            staticOperator.operate(deltaT, entity, body, world);
        }
    }

}
