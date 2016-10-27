package com.brinkman.platformer.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.entity.Coin;
import com.brinkman.platformer.entity.Player;
import com.brinkman.platformer.util.FontUtil;

/**
 * Created by Austin on 10/8/2016.
 */
public class HUD {
    private Stage stage;
    private Table table;
    private Label coinLabel;
    private Label livesLabel;
    private Label.LabelStyle labelStyle;
    private Array<Coin> coins;
    private Player player;

    private static Logger LOGGER = new Logger("HUD", Logger.DEBUG);

    public HUD(OrthographicCamera camera, Array<Coin> coins, Player player) {
        this.player = player;
        stage = new Stage();
        table = new Table();
        this.coins = coins;

        labelStyle = new Label.LabelStyle(FontUtil.getFont("fonts/SF Atarian System Bold.ttf", Color.WHITE, 36), Color.WHITE);
        coinLabel = new Label("", labelStyle);
        livesLabel = new Label("", labelStyle);

        table.setFillParent(true);
        table.top().left().add(livesLabel).width(livesLabel.getWidth()).padRight(125);
        table.add(coinLabel).width(coinLabel.getWidth());

        stage.addActor(table);

        LOGGER.info("Initialized");
    }

    public void render(float delta) {
        if (coins.size > 0) {
            coinLabel.setText("Coins Left: " + coins.size);
        } else {
            coinLabel.setText("Turn In!");
        }

        livesLabel.setText("Lives: " + player.getLives());

        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        stage.dispose();

        LOGGER.info("Disposed");
    }
}
