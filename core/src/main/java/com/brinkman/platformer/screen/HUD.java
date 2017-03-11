package com.brinkman.platformer.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Logger;
import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.component.status.SimpleStatusComponent;
import com.brinkman.platformer.component.status.StatusComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.entity.actor.Player;
import com.brinkman.platformer.util.FontUtil;

/**
 * Created by Austin on 10/8/2016.
 */
public class HUD {
    private final Stage stage;
    private final Label healthLabel;
    private final GameWorld world;

    private static final Logger LOGGER = new Logger(HUD.class.getName(), Logger.DEBUG);

    /**
     * Constructs the Heads-Up Display Object.
     * @param world GameWorld
     */
    public HUD(GameWorld world) {
        stage = new Stage();
        this.world = world;

        LabelStyle labelStyle = new LabelStyle(FontUtil.getBitmapFont("fonts/SF Atarian System Bold.ttf", Color
              .WHITE, 36), Color.WHITE);
        healthLabel = new Label("", labelStyle);

        Table table = new Table();
        table.setFillParent(true);
        table.top().left().add(healthLabel).width(healthLabel.getWidth()).padRight(125);

        stage.addActor(table);

        LOGGER.info("Initialized");
    }

    /**
     * Handles the rendering of the HUD.
     * @param delta float
     */
    public void render(float delta) {
        Player player = null;
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Player) {
                player = (Player) entity;
            }
        }

        healthLabel.setText("Health: " + ((SimpleStatusComponent)player.getComponents().get(StatusComponent.class)).getCurrentHealth());

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
