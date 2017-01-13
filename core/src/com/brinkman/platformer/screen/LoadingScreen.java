package com.brinkman.platformer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.FontUtil;

/**
 * Created by Austin on 12/11/2016.
 */
public class LoadingScreen implements Screen {
    private Stage stage;

    public LoadingScreen() {

    }

    private void drawProgressBar() {
        ShapeRenderer progressBar = new ShapeRenderer();
        progressBar.begin(ShapeRenderer.ShapeType.Filled);
        progressBar.setColor(Color.GREEN);
        progressBar.rect(200, 200, AssetUtil.getProgress() * 400, 100);
        progressBar.end();
    }

    @Override
    public void show() {
        stage = new Stage();
        Table table = new Table();

        Label.LabelStyle labelStyle = new Label.LabelStyle(FontUtil.getBitmapFont("fonts/SF Atarian System Bold Italic.ttf",
                Color.WHITE, 72), Color.WHITE);
        Label label = new Label("Loading", labelStyle);

        table.setFillParent(true);
        table.center().add(label);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        drawProgressBar();
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

    public void dispose() {
        stage.dispose();
    }
}
