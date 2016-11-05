package com.brinkman.platformer.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Actor;
import com.brinkman.platformer.entity.Coin;
import com.brinkman.platformer.entity.Player;
import com.brinkman.platformer.level.Level;
import com.brinkman.platformer.util.FontUtil;

/**
 * Created by Austin on 10/8/2016.
 */
public class HUD {
    private final Stage stage;
    private final Label coinLabel;
    private final Label livesLabel;
    private final Array<Coin> coins;
    private final GameWorld world;

    private static final Logger LOGGER = new Logger("HUD", Logger.DEBUG);

    /**
     * Constructs the Heads-Up Display Object.
     * @param coins Array Coin
     * @param world GameWorld
     */
    public HUD(Array<Coin> coins, GameWorld world) {
        this.coins = coins;
        stage = new Stage();
        this.world = world;

        LabelStyle labelStyle = new LabelStyle(FontUtil.getBitmapFont("fonts/SF Atarian System Bold.ttf", Color
              .WHITE, 36), Color.WHITE);
        coinLabel = new Label("", labelStyle);
        livesLabel = new Label("", labelStyle);

        Table table = new Table();
        table.setFillParent(true);
        table.top().left().add(livesLabel).width(livesLabel.getWidth()).padRight(125);
        table.add(coinLabel).width(coinLabel.getWidth());

        stage.addActor(table);

        LOGGER.info("Initialized");
    }

    /**
     * Handles the rendering of the HUD.
     * @param delta float
     */
    public void render(float delta) {
        if (coins.size > 0) {
            coinLabel.setText("Coins Left: " + coins.size);
        } else {
            coinLabel.setText("Turn In!");
        }

        livesLabel.setText("Lives: " + ((Actor)world.getEntity("player")).getLives());

        stage.act(delta);
        stage.draw();
    }

    /**
     * Disposes of the Stage.
     */
    public void dispose() {
        stage.dispose();

        LOGGER.info("Disposed");
    }
}
