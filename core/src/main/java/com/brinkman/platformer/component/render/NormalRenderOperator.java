package com.brinkman.platformer.component.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.Operator;
import com.brinkman.platformer.component.RootComponent;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.physics.Body;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static com.brinkman.platformer.util.Constants.TO_WORLD_UNITS;

/**
 * @author Austin Brinkman.
 */
public class NormalRenderOperator implements Operator
{
    private final Vector2 tempVector = new Vector2();
    private final Collection<Class<? extends RootComponent>> requiredComponents;
    private final Batch batch;

    public NormalRenderOperator(Batch batch) {
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
        Body body = entity.getComponents().getInstance(PhysicsComponent.class);
        RenderComponent renderComponent = entity.getComponents().getInstance(RenderComponent.class);

        assert (body != null) && (renderComponent != null);

        float originX = 0.0f;
        float originY = 0.0f;
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        float rotation = 0.0f;
        TextureRegion textureRegion = renderComponent.getNormalTextureRegion(deltaT);
        tempVector.set(body.getPosition());
        float width = textureRegion.getRegionWidth() * TO_WORLD_UNITS;
        float height = textureRegion.getRegionHeight() * TO_WORLD_UNITS;
        float x = tempVector.x - ((width - body.getWidth()) / 2);
        float y = tempVector.y - ((height - body.getHeight()) / 2);

        if (textureRegion != null) {
            batch.begin();
            batch.draw(textureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            batch.end();
        }
    }
}
