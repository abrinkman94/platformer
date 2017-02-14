package com.brinkman.platformer.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.physics.Body;

/**
 * @author Caleb Brinkman
 */
public class SpriteRenderComponent implements RenderComponent {
    private final Sprite sprite;

    public SpriteRenderComponent(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void render(float dt, Batch batch, Body body) {
        Vector2 position = body.getPosition();
        Vector2 velocity = body.getVelocity();
        position.x += velocity.x;

        sprite.setPosition(position.x, position.y);

        batch.begin();
        sprite.draw(batch);
        batch.end();
    }
}
