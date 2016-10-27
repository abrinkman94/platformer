package com.brinkman.platformer.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.brinkman.platformer.util.Constants.*;

/**
 * Created by Austin on 10/5/2016.
 */
public class PauseScreen implements Screen {
    private SpriteBatch spriteBatch;
    private Texture texture;

    private boolean drawn;

    public PauseScreen() {
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {

        spriteBatch.enableBlending();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        spriteBatch.begin();
        spriteBatch.draw(texture, 0, 0, APP_WIDTH, APP_HEIGHT);
        spriteBatch.end();
        spriteBatch.disableBlending();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
