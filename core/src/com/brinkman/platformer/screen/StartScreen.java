package com.brinkman.platformer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.FontUtil;
import com.brinkman.platformer.util.ParallaxBackground;
import com.brinkman.platformer.util.ParallaxLayer;

/**
 * Created by Austin on 10/19/2016.
 */
public class StartScreen implements Screen {

    private Stage stage;
    private Label platformerLabel;
    private Label.LabelStyle platformerLabelStyle;
    private TextButton startButton;
    private TextButton.TextButtonStyle startButtonStyle;
    private Texture buttonUp;
    private Texture buttonDown;
    private Texture buttonOver;
    private Texture clouds;
    private TextureRegion textureRegion;
    private ParallaxBackground background;
    private Music music;
    private Sound buttonSound;

    private boolean initiateGameScreen = false;

    public StartScreen() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        playMusic();

        buttonUp = new Texture("hud/startscreen/button.png");
        buttonDown = new Texture("hud/startscreen/button3.png");
        buttonOver = new Texture("hud/startscreen/button2.png");
        clouds = new Texture("hud/startscreen/clouds.png");
        clouds.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
        textureRegion = new TextureRegion(clouds);

        createBackground();
        createPlatformerLabel();
        createStartButton();

        Table table = new Table();
        table.setFillParent(true);
        table.center().add(platformerLabel);
        table.row();
        table.row();
        table.add(startButton).width(250).height(100);

        stage.addActor(table);
    }

    public boolean isInitiateGameScreen() { return initiateGameScreen; }

    private void createBackground() {
        background = new ParallaxBackground(new ParallaxLayer[]{
                new ParallaxLayer(textureRegion, new Vector2(1, 1), new Vector2(0, 0)),
        }, 1080, 720, new Vector2(50, 0));
    }

    private void createPlatformerLabel() {
        platformerLabelStyle = new Label.LabelStyle(FontUtil.getBitmapFont("fonts/SF Atarian System Extended Bold.ttf",
                Color.FOREST, 72), Color.FOREST);
        platformerLabel = new Label("PLATFORMER!", platformerLabelStyle);
    }

    private void createStartButton() {
        startButtonStyle = new TextButton.TextButtonStyle();
        startButtonStyle.font = FontUtil.getBitmapFont("fonts/SF Atarian System Extended Bold.ttf",
                Color.BLACK, 60);
        startButtonStyle.up = new NinePatchDrawable(new NinePatch(buttonUp, 0, 0, 0, 0));
        startButtonStyle.down = new NinePatchDrawable(new NinePatch(buttonDown, 0, 0, 0, 0));
        startButtonStyle.over = new NinePatchDrawable(new NinePatch(buttonOver, 0, 0, 0, 0));
        startButton = new TextButton("Start", startButtonStyle);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playButtonSound();
                music.stop();
                initiateGameScreen = true;
            }
        });
    }

    private void playMusic() {
        music = (Music) AssetUtil.getAsset("audio/Eclipse.mp3", Music.class);
        music.setVolume(0.1f);
        music.play();
        music.setLooping(true);
    }

    private void playButtonSound() {
        buttonSound = (Sound) AssetUtil.getAsset("audio/heavy_throw_switch.mp3", Sound.class);
        buttonSound.play(0.1f);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        background.render(delta);
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
        stage.dispose();
    }
}
