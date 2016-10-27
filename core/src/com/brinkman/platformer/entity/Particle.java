package com.brinkman.platformer.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Austin on 10/6/2016.
 */
public class Particle {
    private SpriteBatch batch;
    private ParticleEffect particleEffect;

    public Particle(String particleLocation, SpriteBatch batch) {
        this.batch = batch;

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal(particleLocation), Gdx.files.internal(""));

    }

    public void draw(float delta, Actor actor) {
        particleEffect.getEmitters().first().setPosition(actor.getPosition().x + (actor.getWidth() / 2),actor.getPosition().y);
        particleEffect.getEmitters().first().getSpawnHeight().setHigh(0, 1);
        particleEffect.update(delta);

        batch.begin();
        particleEffect.draw(batch);
        batch.end();
    }
}
