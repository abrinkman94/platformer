package com.brinkman.platformer.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.brinkman.platformer.util.FontUtil;

/**
 * Created by Austin on 10/19/2016.
 */
public class StartScreen implements Screen {

    private Stage stage;
    private Label.LabelStyle labelStyle;
    private Label startLabel;

    public StartScreen() {
        stage = new Stage();
        labelStyle = new Label.LabelStyle(FontUtil.getFont("fonts/SF Atarian System Bold.ttf", Color.WHITE, 72), Color.WHITE);
        startLabel = new Label("Start", labelStyle);

        Table table = new Table();
        table.setFillParent(true);
        table.center().add(startLabel);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
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
