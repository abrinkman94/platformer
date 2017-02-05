package com.brinkman.platformer.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.item.ItemType;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.util.AssetUtil;
import com.brinkman.platformer.util.FontUtil;
import com.brinkman.platformer.util.TexturePaths;

/**
 * Created by Austin on 10/8/2016.
 */
public class HUD {
    private final Stage stage;
    private final Label coinLabel;
    private final Label livesLabel;
    private final Label levelLabel;
    private final Image keyImage;
    private final GameWorld world;

    private static final Logger LOGGER = new Logger(HUD.class.getName(), Logger.DEBUG);

    /**
     * Constructs the Heads-Up Display Object.
     * @param world GameWorld
     */
    public HUD(GameWorld world) {
        stage = new Stage();
        this.world = world;
        keyImage = new Image((Texture) AssetUtil.getAsset(TexturePaths.HUD_KEY_TEXTURE, Texture.class));
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
        if (world.getNumberOfCoins() > 0) {
            coinLabel.setText("Coins Left: " + world.getNumberOfCoins());
        } else {
            coinLabel.setText("Turn In!");
        }

        Player player = null;
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Player) {
                player = (Player) entity;
            }
        }

        //Update lives and level labels
        livesLabel.setText("Lives: " + player.getLives());
        levelLabel.setText("Level: " + world.getLevel().getLevelNumber());

        //Update key image
        if (world.getLevel().hasKey()) {
            if (player.getInventory().values().contains(ItemType.KEY) && !keyImage.isVisible()) {
                    keyImage.setVisible(true);
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
