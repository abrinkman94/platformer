package com.brinkman.platformer.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.*;
import com.brinkman.platformer.util.FontUtil;

/**
 * Created by Austin on 10/8/2016.
 */
public class HUD {
    private final Stage stage;
    private final Label coinLabel;
    private final Label livesLabel;
    private final Label levelLabel;
    private final Image keyImage;
    private final Array<Coin> coins;
    private final GameWorld world;

    private static final Logger LOGGER = new Logger(HUD.class.getName(), Logger.DEBUG);

    /**
     * Constructs the Heads-Up Display Object.
     * @param coins Array Coin
     * @param world GameWorld
     */
    public HUD(Array<Coin> coins, GameWorld world) {
        this.coins = coins;
        stage = new Stage();
        this.world = world;

        keyImage = new Image(new Texture("terrain/Object/HUD key.png"));
        keyImage.setVisible(false);

        LabelStyle labelStyle = new LabelStyle(FontUtil.getBitmapFont("fonts/SF Atarian System Bold.ttf", Color
              .WHITE, 36), Color.WHITE);
        coinLabel = new Label("", labelStyle);
        livesLabel = new Label("", labelStyle);
        levelLabel = new Label("", labelStyle);

        Table table = new Table();
        table.setFillParent(true);
        table.add(levelLabel).width(levelLabel.getWidth()).padRight(125);
        table.top().left().add(livesLabel).width(livesLabel.getWidth()).padRight(125);
        table.add(coinLabel).width(coinLabel.getWidth()).padRight(750);
        table.add(keyImage);

        stage.addActor(table);

        LOGGER.info("Initialized");
    }

    /**
     * Handles the rendering of the HUD.
     * @param delta float
     */
    public void render(float delta) {
        //Update coin label
        if (coins.size > 0) {
            coinLabel.setText("Coins Left: " + coins.size);
        } else {
            coinLabel.setText("Turn In!");
        }

        //Update lives and level labels
        livesLabel.setText("Lives: " + ((Actor)world.getEntityByValue("player")).getLives());
        levelLabel.setText("Level: " + world.getLevel().getLevelNumber());

        //Update key image
        if (world.getLevel().getHasKey()) {
            for (Item item : ((Player) world.getEntityByValue("player")).getItems()) {
                if (item.getItemType() == ItemType.KEY && !keyImage.isVisible()) {
                    keyImage.setVisible(true);
                }
            }
        } else {
            keyImage.setVisible(false);
        }

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
