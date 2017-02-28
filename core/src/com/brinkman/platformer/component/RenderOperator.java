package com.brinkman.platformer.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.physics.MotileBody;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @author Caleb Brinkman
 */
public class RenderOperator implements Operator {
    private final Collection<Class<? extends RootComponent>> requiredComponents;
    private final Batch batch;

    public RenderOperator(Batch batch) {
        this.batch = batch;
        requiredComponents = new LinkedList<>();
        requiredComponents.add(RenderComponent.class);
        requiredComponents.add(PhysicsComponent.class);
    }

    @Override
    public Collection<Class<? extends RootComponent>> getRequiredComponents() {
        return Collections.unmodifiableCollection(requiredComponents);
    }

    @Override
    public void operate(float deltaT, Entity entity, GameWorld world) {
        MotileBody body = (MotileBody) entity.getComponents().getInstance(PhysicsComponent.class);
        RenderComponent renderComponent = entity.getComponents().getInstance(RenderComponent.class);

        assert (body != null) && (renderComponent != null);

        renderComponent.render(deltaT, batch, body);
    }

}
